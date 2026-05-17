package de.myshop.order.controllers;

import de.myshop.order.dtos.OrderRequest;
import de.myshop.order.dtos.OrderResponse;
import de.myshop.order.models.Category;
import de.myshop.order.models.Order;
import de.myshop.order.services.OrderService;
import de.myshop.order.services.mappers.OrderMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/customer-orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody OrderRequest request,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey) {

        boolean isReplay = false;
        if (idempotencyKey != null) {
            isReplay = orderService.isAlreadyProcessed(idempotencyKey);
        }

        Order order = orderService.createOrder(request, idempotencyKey);
        OrderResponse response = OrderMapper.mapToResponse(order);

        var responseBuilder = ResponseEntity.status(isReplay ? HttpStatus.OK : HttpStatus.CREATED);

        if (isReplay) {
            responseBuilder.header("X-Idempotency-Replayed", "true");
        }

        return responseBuilder.body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable UUID id) {
        Order order = orderService.getOrder(id);
        return ResponseEntity.ok(OrderMapper.mapToResponse(order));
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> listOrders(
            @RequestParam(required = false) Category category,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "20") int limit) {

        Page<Order> page = orderService.listOrders(category, offset, limit);

        Map<String, Object> response = new HashMap<>();
        response.put("items", page.getContent().stream().map(OrderMapper::mapToResponse).toList());
        response.put("total", page.getTotalElements());
        response.put("limit", limit);
        response.put("offset", offset);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<OrderResponse> patchOrder(
            @PathVariable UUID id,
            @RequestBody Map<String, Object> updates) {

        Order updatedOrder = orderService.patchOrder(id, updates);
        return ResponseEntity.ok(OrderMapper.mapToResponse(updatedOrder));
    }
}
