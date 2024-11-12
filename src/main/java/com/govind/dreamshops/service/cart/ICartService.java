package com.govind.dreamshops.service.cart;

import com.govind.dreamshops.dto.CartDto;
import com.govind.dreamshops.model.Cart;
import com.govind.dreamshops.model.User;

import java.math.BigDecimal;

public interface ICartService {
    Cart getCart(Long id);

    void clearCart(Long id);

    BigDecimal getTotalPrice(Long id);

    Cart initializeNewCart(User user);

    Cart getCartByUserId(Long userId);

    CartDto mapCartToDto(Cart cart);
}
