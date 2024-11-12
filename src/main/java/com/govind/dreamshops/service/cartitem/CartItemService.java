package com.govind.dreamshops.service.cartitem;

import com.govind.dreamshops.exceptions.ResourceNotFoundException;
import com.govind.dreamshops.model.Cart;
import com.govind.dreamshops.model.CartItem;
import com.govind.dreamshops.model.Product;
import com.govind.dreamshops.repository.CartItemRepository;
import com.govind.dreamshops.repository.CartRepository;
import com.govind.dreamshops.service.cart.ICartService;
import com.govind.dreamshops.service.product.IProductService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartItemService implements ICartItemService {

    private final CartItemRepository cartItemRepository;
    private final ICartService cartService;
    private final IProductService productService;
    private final CartRepository cartRepository;


    @Transactional
    @Override
    public void addItemToCart(Long cartId, Long productId, int quantity) {
        // 1. Get the cart
        // 2. Get the product
        // 3. Check if the product already exists in the cart
        // 4. If no, then initiate a new CartItem entry
        // 5. If yes, increase its quantity with the requested quantity
        Cart cart = cartService.getCart(cartId);
        Product product = productService.getProductById(productId);
        CartItem cartItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(new CartItem());

        if (cartItem.getId() == null) {
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cartItem.setUnitPrice(product.getPrice());
        } else {
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        }
        cartItem.setTotalPrice();
        cart.addItem(cartItem);
        cartItemRepository.save(cartItem);
        cartRepository.save(cart);
    }


    @Override
    public void removeItemFromCart(Long cartId, Long productId) {
        Cart cart = cartService.getCart(cartId);
        CartItem itemToRemove = getCartItem(cartId, productId);
        cart.removeItem(itemToRemove);
        cartRepository.save(cart);
    }


    @Override
    public void updateItemQuantity(Long cartId, Long productId, int quantity) {
        Cart cart = cartService.getCart(cartId);
        cart.getItems()
                .stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .ifPresent(item -> {
                    item.setQuantity(quantity);
                    item.setUnitPrice(item.getProduct().getPrice());
                    item.setTotalPrice();
                });
        cart.updateTotalAmount();
        cartRepository.save(cart);
    }

    @Override
    public CartItem getCartItem(Long cartId, Long productId) {
        Cart cart = cartService.getCart(cartId);
        return cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Item not found!"));
    }


}
