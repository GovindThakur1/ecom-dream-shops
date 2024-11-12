package com.govind.dreamshops.service.order;

import com.govind.dreamshops.dto.OrderDto;
import com.govind.dreamshops.model.Order;

import java.util.List;

public interface IOrderService {
    OrderDto placeOrder(Long userId);
    OrderDto getOrder(Long orderId);

    List<OrderDto> getOrdersByUser(Long userId);

    OrderDto mapOrderToDto(Order order);
}
