package de.myshop.order.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.myshop.order.clients.CatalogClient;
import de.myshop.order.dtos.OrderItemRequest;
import de.myshop.order.dtos.OrderRequest;
import de.myshop.order.models.*;
import de.myshop.order.repositories.OrderRepository;
import de.myshop.order.services.exceptions.IdempotencyConflictException;
import de.myshop.order.services.exceptions.IllegalStateTransitionException;
import de.myshop.order.services.exceptions.OrderNotFoundException;
import de.myshop.order.services.mappers.OrderMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final CatalogClient catalogClient;

    @Transactional
    public Order createOrder(OrderRequest request, String idempotencyKey) {
        if (idempotencyKey != null) {
            return orderRepository.findByIdempotencyKey(idempotencyKey)
                    .map(existing -> handleDuplicateRequest(existing, request))
                    .orElseGet(() -> saveNewOrder(request, idempotencyKey));
        }
        return saveNewOrder(request, null);
    }

    private Order saveNewOrder(OrderRequest request, String key) {
        verifyCatalogItems(request.orderItems());
        Order order = OrderMapper.mapToEntity(request);
        order.setIdempotencyKey(key);
        order.setState(OrderState.DRAFT);
        return orderRepository.save(order);
    }

    @Transactional
    public Order patchOrder(UUID id, Map<String, Object> updates) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id.toString()));

        if (!order.getState().isEditable()) {
            boolean tryingToEditData = updates.keySet().stream()
                    .anyMatch(key -> !key.equals("state"));

            if (tryingToEditData) {
                throw new IllegalStateException("Updates to data fields are not allowed in state " + order.getState());
            }
        }

        if (updates.containsKey("state")) {
            String stateStr = updates.get("state").toString().toUpperCase();
            try {
                OrderState newState = OrderState.valueOf(stateStr);
                if (!order.getState().canTransitionTo(newState)) {
                    throw new IllegalStateTransitionException(order.getState().name(), newState.name());
                }
                order.setState(newState);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid state: " + stateStr + ". Use DRAFT, PREVIEW, SUBMITTED, or CONFIRMED.");
            }
        }

        if (order.getState() == OrderState.SUBMITTED || order.getState() == OrderState.CONFIRMED) {
            if (updates.keySet().stream().anyMatch(key -> !key.equals("state"))) {
                throw new IllegalStateException("Only state transitions allowed after submission");
            }
        }

        applyPatch(order, updates);

        if (updates.containsKey("orderItems")) {
            verifyCatalogItemsFromEntities(order.getOrderItems());
        }

        return orderRepository.save(order);
    }

    public Order getOrder(UUID id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id.toString()));
    }

    public Page<Order> listOrders(Category category, int offset, int limit) {
        PageRequest pageRequest = PageRequest.of(offset / limit, limit, Sort.by("createdAt").descending());
        if (category != null) {
            return orderRepository.findByCategory(category, pageRequest);
        }
        return orderRepository.findAll(pageRequest);
    }

    public boolean isAlreadyProcessed(String key) {
        return orderRepository.findByIdempotencyKey(key).isPresent();
    }

    private void applyPatch(Order order, Map<String, Object> updates) {

        if (updates.containsKey("category")) {
            order.setCategory(Category.valueOf(updates.get("category").toString().toUpperCase()));
        }
        if (updates.containsKey("customerId")) {
            order.setCustomerId(updates.get("customerId").toString());
        }
        if (updates.containsKey("siteId")) {
            order.setSiteId(updates.get("siteId").toString());
        }

        if (updates.containsKey("paymentMethod")) {
            Map<String, Object> pmUpdate = (Map<String, Object>) updates.get("paymentMethod");
            PaymentMethod pm = order.getPaymentMethod();
            if (pm == null) pm = new PaymentMethod();

            if (pmUpdate.containsKey("type")) {
                pm.setType(PaymentType.valueOf(pmUpdate.get("type").toString().toUpperCase()));
            }
            if (pmUpdate.containsKey("iban")) {
                pm.setIban(pmUpdate.get("iban") != null ? pmUpdate.get("iban").toString() : null);
            }
            order.setPaymentMethod(pm);
        }

        if (updates.containsKey("orderItems")) {
            List<Map<String, Object>> itemsUpdate = (List<Map<String, Object>>) updates.get("orderItems");

            order.getOrderItems().clear();

            List<OrderItem> newItems = itemsUpdate.stream().map(itemMap -> {
                OrderItem item = new OrderItem();
                item.setProductOfferingId(itemMap.get("productOfferingId").toString());
                item.setQuantity((Integer) itemMap.get("quantity"));
                return item;
            }).toList();

            order.getOrderItems().addAll(newItems);
        }
    }

    private void verifyCatalogItems(List<OrderItemRequest> items) {
        List<String> ids = items.stream().map(OrderItemRequest::productOfferingId).toList();
        catalogClient.verifyProducts(ids);
    }

    private void verifyCatalogItemsFromEntities(List<OrderItem> items) {
        List<String> ids = items.stream().map(OrderItem::getProductOfferingId).toList();
        catalogClient.verifyProducts(ids);
    }

    private Order handleDuplicateRequest(Order existing, OrderRequest request) {
        if (isPayloadIdentical(existing, request)) return existing;
        throw new IdempotencyConflictException("Different payload for same key");
    }

    private boolean isPayloadIdentical(Order existing, OrderRequest request) {
        return existing.getCustomerId().equals(request.customerId()) &&
                existing.getSiteId().equals(request.siteId()) &&
                existing.getCategory() == request.category() &&
                hasSameItems(existing.getOrderItems(), request.orderItems()) &&
                existing.getPaymentMethod().getType() == request.paymentMethod().type();
    }

    private boolean hasSameItems(List<OrderItem> existingItems, List<OrderItemRequest> requestedItems) {
        if (existingItems.size() != requestedItems.size()) return false;

        for (int i = 0; i < existingItems.size(); i++) {
            OrderItem existing = existingItems.get(i);
            OrderItemRequest requested = requestedItems.get(i);

            if (!existing.getProductOfferingId().equals(requested.productOfferingId()) ||
                    existing.getQuantity() != requested.quantity()) {
                return false;
            }
        }
        return true;
    }
}
