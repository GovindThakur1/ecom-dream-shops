package com.govind.dreamshops.service.cart;

import com.govind.dreamshops.dto.CartDto;
import com.govind.dreamshops.exceptions.ResourceNotFoundException;
import com.govind.dreamshops.model.Cart;
import com.govind.dreamshops.model.User;
import com.govind.dreamshops.repository.CartItemRepository;
import com.govind.dreamshops.repository.CartRepository;
import com.govind.dreamshops.repository.UserRepository;
import com.govind.dreamshops.service.user.IUserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class CartService implements ICartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final IUserService userService;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    // Create cart id manually for now
    private final AtomicLong cartIdGenerator = new AtomicLong(0);


    @Override
    public Cart getCart(Long id) {
        return cartRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found!"));
    }

    @Transactional
    @Override
    public void clearCart(Long id) {
        Cart cart = getCart(id);
        cart.getItems().clear();
        cartItemRepository.deleteAllByCartId(id); // Delete the items after clearing
        cart.setTotalAmount(BigDecimal.ZERO); // Reset the totalAmount
        cartRepository.save(cart);
    }

    @Override
    public BigDecimal getTotalPrice(Long cartId) {
        Cart cart = getCart(cartId);
        return cart.getTotalAmount();
    }


    @Override
    public Cart initializeNewCart(User user) {
        return Optional.ofNullable(getCartByUserId(user.getId()))
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setUser(user);
                    user.setCart(cart);

                    Cart savedCart = cartRepository.save(cart);
                    userRepository.save(user);
                    return savedCart;
                });
    }

    @Override
    public Cart getCartByUserId(Long userId) {
        User user = userService.getUserById(userId);
        return cartRepository.findByUserId(userId);
    }


    @Override
    public CartDto mapCartToDto(Cart cart) {
        return modelMapper.map(cart, CartDto.class);
    }
}
















