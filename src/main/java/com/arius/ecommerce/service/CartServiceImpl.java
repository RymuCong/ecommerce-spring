package com.arius.ecommerce.service;

import com.arius.ecommerce.dto.CartDTO;
import com.arius.ecommerce.dto.ProductDTO;
import com.arius.ecommerce.entity.Cart;
import com.arius.ecommerce.entity.CartItem;
import com.arius.ecommerce.entity.Product;
import com.arius.ecommerce.entity.User;
import com.arius.ecommerce.exception.APIException;
import com.arius.ecommerce.exception.ResourceNotFoundException;
import com.arius.ecommerce.repository.CartItemRepository;
import com.arius.ecommerce.repository.CartRepository;
import com.arius.ecommerce.repository.ProductRepository;
import com.arius.ecommerce.repository.UserRepository;
import com.arius.ecommerce.utils.CommonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartServiceImpl implements CartService{

    private final UserRepository userRepository;

    private final CartRepository cartRepository;

    private final ProductRepository productRepository;

    private final CartItemRepository cartItemRepository;

    @Autowired
    public CartServiceImpl(UserRepository userRepository, CartRepository cartRepository, ProductRepository productRepository, CartItemRepository cartItemRepository) {
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.cartItemRepository = cartItemRepository;
    }

    @Override
    public CartDTO addProductToCart(String userEmail, Long productId, int quantity) {
        User user = userRepository.findByEmail(userEmail);

        if(user == null){
            throw new ResourceNotFoundException("User","email",userEmail);
        }

        Cart cart = cartRepository.findByUserUserId(user.getUserId());

        if (cart == null) {
            cart = new Cart();
            cart.setUser(user);
            cart.setTotalPrice(0L);
            cart = cartRepository.save(cart);
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product","productId",productId));

        CartItem cartItem = cartItemRepository.findCartItemByProductIAndCartId(cart.getCartId(),productId);

        if(cartItem != null){
            throw new APIException("Product " + product.getProductName() + " already exists in the cart");
        }

        if(product.getQuantity() == 0){
            throw new APIException(product.getProductName() + " is not available");
        }

        if(product.getQuantity() < quantity){
            throw new APIException("Please make an order " + product.getProductName() +
                    " less than or equal to the quantity"+ product.getQuantity());
        }

        CartItem newCartItem = new CartItem();
        newCartItem.setProduct(product);
        newCartItem.setCart(cart);
        newCartItem.setQuantity(quantity);
        newCartItem.setDiscount(product.getDiscount());
        newCartItem.setSpecialPrice(product.getSpecialPrice());

        cartItemRepository.save(newCartItem);

        product.setQuantity(product.getQuantity() - quantity);
        cart.setTotalPrice(cart.getTotalPrice() + (newCartItem.getSpecialPrice() * quantity));

        productRepository.save(product);
        cartRepository.save(cart);

        CartDTO cartDTO = CommonMapper.INSTANCE.toCartDTO(cart);

        List<ProductDTO> productDTOS = cart.getCartItems().stream()
                .map(p -> CommonMapper.INSTANCE.toProductDTO(p.getProduct())).toList();

        cartDTO.setProducts(productDTOS);
        return cartDTO;
    }

    @Override
    public List<CartDTO> getCarts() {
        List<Cart> carts = cartRepository.findAll();

        if (carts.isEmpty()){
            throw new APIException("No Cart Exists");
        }

        List<CartDTO> cartDTOS = carts.stream().map(cart -> {
            CartDTO cartDTO = CommonMapper.INSTANCE.toCartDTO(cart);

            List<ProductDTO> productDTOS = cart.getCartItems().stream().map(
                    prod -> CommonMapper.INSTANCE.toProductDTO(prod.getProduct())
            ).toList();

            cartDTO.setProducts(productDTOS);

            return cartDTO;
        }).toList();

        return cartDTOS;
    }

    @Override
    public CartDTO getCartById(String emailId) {
        User user = userRepository.findByEmail(emailId);

        if(user == null){
            throw new ResourceNotFoundException("User","emailId",emailId);
        }

        Cart cart = cartRepository.findCartByEmailAndCartId(emailId,user.getCart().getCartId());

        CartDTO cartDTO = CommonMapper.INSTANCE.toCartDTO(cart);

        List<ProductDTO> productDTOS = cart.getCartItems().stream().map(
                prod->CommonMapper.INSTANCE.toProductDTO(prod.getProduct())
        ).toList();

        cartDTO.setProducts(productDTOS);

        return cartDTO;
    }

    @Override
    public CartDTO updateProductQuantity(String emailId, Long productId, int quantity) {
        User user = userRepository.findByEmail(emailId);

        if (user == null) {
            throw new ResourceNotFoundException("User", "emailId", emailId);
        }

        Cart cart = cartRepository.findById(user.getCart().getCartId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", user.getCart().getCartId()));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        if (product.getQuantity() == 0) {
            throw new APIException(product.getProductName() + " is not available");
        }

        if (product.getQuantity() < quantity) {
            throw new APIException("Please make an order " + product.getProductName() +
                    " less than or equal to the quantity " + product.getQuantity());
        }

        CartItem cartItem = cartItemRepository.findCartItemByProductIAndCartId(cart.getCartId(), productId);

        if (cartItem == null) {
            throw new APIException("Product " + product.getProductName() + " not available in the cart");
        }

        int cartItemQuantity = cartItem.getQuantity() + quantity;

        if (product.getQuantity() <= cartItemQuantity) {
            throw new APIException("You have reached your limit");
        }

        double cartPrice = cart.getTotalPrice() - (cartItem.getSpecialPrice() * cartItem.getQuantity());

        cartItem.setSpecialPrice(product.getSpecialPrice());
        cartItem.setQuantity(cartItemQuantity);
        cartItem.setDiscount(product.getDiscount());

        cart.setTotalPrice((long) (cartPrice + (cartItem.getSpecialPrice() * cartItemQuantity)));
        cartItemRepository.save(cartItem);

        product.setQuantity(product.getQuantity() - quantity);
        productRepository.save(product);
        cartRepository.save(cart);

        CartDTO cartDTO = CommonMapper.INSTANCE.toCartDTO(cart);

        List<ProductDTO> productDTOS = cart.getCartItems().stream().map(
                prod -> CommonMapper.INSTANCE.toProductDTO(prod.getProduct())
        ).toList();

        cartDTO.setProducts(productDTOS);

        return cartDTO;
    }

    @Override
    public String deleteProductFromCart(String emailId, Long productId) {
        User user = userRepository.findByEmail(emailId);

        if (user == null){
            throw new ResourceNotFoundException("User","emailId",emailId);
        }

        Cart cart = cartRepository.findCartByEmailAndCartId(emailId,user.getCart().getCartId());

        CartItem cartItem = cartItemRepository.findCartItemByProductIAndCartId(cart.getCartId(),productId);

        cart.setTotalPrice(cart.getTotalPrice() - (cartItem.getSpecialPrice() * cartItem.getQuantity()));

        Product product = cartItem.getProduct();
        product.setQuantity(product.getQuantity() + cartItem.getQuantity());

        cartItemRepository.deleteCartItemByProductIdAndCartId(cart.getCartId(),productId);
        return "Product " + cartItem.getProduct().getProductName() + " deleted successfully";
    }

    @Override
    public void deleteProductFromCartUsingCartId(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart","cartId",cartId));

        CartItem cartItem = cartItemRepository.findCartItemByProductIAndCartId(cartId,productId);

        cart.setTotalPrice(cart.getTotalPrice() - (cartItem.getSpecialPrice() * cartItem.getQuantity()));

        Product product = cartItem.getProduct();
        product.setQuantity(product.getQuantity() + cartItem.getQuantity());

        cartItemRepository.deleteCartItemByProductIdAndCartId(cart.getCartId(),productId);
    }
}