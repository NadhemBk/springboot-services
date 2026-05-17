package de.myshop.order.services.mappers;

import de.myshop.order.dtos.OrderItemResponse;
import de.myshop.order.dtos.OrderRequest;
import de.myshop.order.dtos.OrderResponse;
import de.myshop.order.models.Order;
import de.myshop.order.models.OrderItem;
import de.myshop.order.models.PaymentMethod;

import java.util.List;

public class OrderMapper {

    private OrderMapper() {}
    public static OrderResponse mapToResponse(Order entity) {
        return new OrderResponse(
                entity.getId(),
                entity.getState(),
                entity.getCategory(),
                new OrderResponse.CustomerResponse(entity.getCustomerId()),
                new OrderResponse.SiteResponse(entity.getSiteId()),
                entity.getOrderItems().stream()
                        .map(item -> new OrderItemResponse(item.getProductOfferingId(), item.getQuantity()))
                        .toList(),
                new OrderResponse.PaymentMethodResponse(
                        entity.getPaymentMethod().getType().name(),
                        entity.getPaymentMethod().getIban()
                ),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public static Order mapToEntity(OrderRequest request) {
        Order order = new Order();
        order.setCustomerId(request.customerId());
        order.setSiteId(request.siteId());
        order.setCategory(request.category());

        PaymentMethod pm = new PaymentMethod();
        pm.setType(request.paymentMethod().type());
        pm.setIban(request.paymentMethod().iban());
        order.setPaymentMethod(pm);

        List<OrderItem> items = request.orderItems().stream()
                .map(itemDto -> {
                    OrderItem item = new OrderItem();
                    item.setProductOfferingId(itemDto.productOfferingId());
                    item.setQuantity(itemDto.quantity());
                    return item;
                })
                .toList();

        order.setOrderItems(items);
        return order;
    }

}
