package com.govind.dreamshops.service.order;

import com.govind.dreamshops.dto.OrderDto;
import com.govind.dreamshops.enums.OrderStatus;
import com.govind.dreamshops.exceptions.ResourceNotFoundException;
import com.govind.dreamshops.model.Cart;
import com.govind.dreamshops.model.Order;
import com.govind.dreamshops.model.OrderItem;
import com.govind.dreamshops.model.Product;
import com.govind.dreamshops.repository.OrderRepository;
import com.govind.dreamshops.repository.ProductRepository;
import com.govind.dreamshops.service.cart.ICartService;
import com.govind.dreamshops.service.user.IUserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ICartService cartService;
    private final IUserService userService;
    private final ModelMapper modelMapper;

    @Override
    public OrderDto placeOrder(Long userId) {
        Cart cart = cartService.getCartByUserId(userId);

        Order order = createOrder(cart);
        List<OrderItem> orderItems = createOrderItems(order, cart);

        order.setOrderItems(new HashSet<>(orderItems));
        order.setTotalAmount(calculateTotalAmount(orderItems));

        Order savedOrder = orderRepository.save(order);

        cartService.clearCart(cart.getId());
        return mapOrderToDto(savedOrder);
    }


    // Helper methods required for creating an order.
    private Order createOrder(Cart cart) {
        Order order = new Order();
        order.setUser(cart.getUser());
        order.setOrderStatus(OrderStatus.PENDING);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }


    private List<OrderItem> createOrderItems(Order order, Cart cart) {
        return cart.getItems()
                .stream()
                .map(cartItem -> {
                    Product product = cartItem.getProduct();
                    product.setInventory(product.getInventory() - cartItem.getQuantity());
                    productRepository.save(product);
                    return new OrderItem(
                            order,
                            product,
                            cartItem.getQuantity(),
                            cartItem.getUnitPrice()
                    );
                }).toList();
    }


    private BigDecimal calculateTotalAmount(List<OrderItem> orderItems) {
        return orderItems.stream()
                .map(orderItem -> orderItem
                        .getPrice()
                        .multiply(new BigDecimal(orderItem.getQuantity()))
                ).reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    @Override
    public OrderDto getOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .map(this::mapOrderToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found."));
    }


    @Override
    public List<OrderDto> getOrdersByUser(Long userId) {
        return Optional.of(userService.getUserById(userId))
                .map(user -> orderRepository.findByUserId(userId))
                .map(orders -> orders.stream()
                        .map(this::mapOrderToDto)
                        .collect(Collectors.toList()))
                .orElseThrow(() -> new ResourceNotFoundException("User not found!"));
    }


    @Override
    public OrderDto mapOrderToDto(Order order) {
        return modelMapper.map(order, OrderDto.class);
    }

}

















