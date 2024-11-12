package com.govind.dreamshops.controller;

import com.govind.dreamshops.dto.OrderDto;
import com.govind.dreamshops.exceptions.ResourceNotFoundException;
import com.govind.dreamshops.model.Order;
import com.govind.dreamshops.response.ApiResponse;
import com.govind.dreamshops.service.order.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/orders")
public class OrderController {
    private final IOrderService orderService;

    @PostMapping("/order")
    public ResponseEntity<ApiResponse> createOrder(@RequestParam Long userId) {
        try {
            OrderDto orderDto = orderService.placeOrder(userId);
            return ResponseEntity.ok(new ApiResponse("Success!", orderDto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Error occurred!", e.getMessage()));
        }
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse> getOrderById(@PathVariable Long orderId) {
        try {
            OrderDto order = orderService.getOrder(orderId);
            return ResponseEntity.ok(new ApiResponse("Success!", order));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/order/user/{userId}")
    public ResponseEntity<ApiResponse> getOrderByUserId(@PathVariable Long userId) {
        try {
            List<OrderDto> orders = orderService.getOrdersByUser(userId);
            return ResponseEntity.ok(new ApiResponse("Success!", orders));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }


}












