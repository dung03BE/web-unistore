package com.dung.UniStore.service;

import com.dung.UniStore.dto.response.CartItemResponse;
import com.dung.UniStore.dto.response.CartResponse;
import com.dung.UniStore.dto.response.ProductDetailsResponse;
import com.dung.UniStore.dto.response.ProductResponse;
import com.dung.UniStore.entity.*;
import com.dung.UniStore.exception.ApiException;
import com.dung.UniStore.exception.AppException;
import com.dung.UniStore.exception.ErrorCode;
import com.dung.UniStore.mapper.ProductMapper;
import com.dung.UniStore.repository.*;
import com.dung.UniStore.utils.AuthUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService implements ICartService {
    private final IProductDetailsRepository productDetailsRepository;
    private final ProductMapper productMapper;
    private final ICartRepository cartRepository;
    private final IProductRepository productRepository;
    private final ICartItemRepository cartItemRepository;
//    private final InventoryRepository inventoryRepository;
    private final ModelMapper modelMapper;
    private final AuthUtil authUtil;
    @PersistenceContext
    private EntityManager entityManager;
    @Override
    public CartResponse addProToCart(Long productId, Integer quantity, String color) throws Exception {
        Cart cart = createCart(); // Kiểm tra giỏ hàng có tồn tại không

        Product product = productRepository.findById(Math.toIntExact(productId))
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

        // Tìm sản phẩm trong giỏ hàng theo productId và color
        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartIdAndColor(cart.getCartId(), productId, color);


        if (cartItem != null) {
            // Nếu sản phẩm đã tồn tại trong giỏ, cộng dồn số lượng
            int updatedQuantity = cartItem.getQuantity() + quantity;
            cartItem.setQuantity(updatedQuantity);
            cartItemRepository.save(cartItem);

        } else {
            CartItem newCartItem = new CartItem();
            newCartItem.setProduct(product);
            newCartItem.setCart(cart);
            newCartItem.setQuantity(quantity);
            newCartItem.setColor(color);
            newCartItem.setDiscount(product.getDiscount() != null ? product.getDiscount() : 0.0);
            double productPrice = product.getSpecialPrice() != null ? product.getSpecialPrice() : product.getPrice();
            newCartItem.setProductPrice(productPrice);

            cartItemRepository.save(newCartItem);

        }
        // QUAN TRỌNG: Tính toán totalPrice từ danh sách cartItems mới nhất
        List<CartItem> updatedCartItems = cartItemRepository.findByCart_CartId(cart.getCartId());

        // Tính toán tổng giá trực tiếp từ danh sách cart items đã cập nhật
        double totalPrice = 0.0;
        for (CartItem item : updatedCartItems) {
            totalPrice += item.getProductPrice() * item.getQuantity();
        }
        // Cập nhật totalPrice và lưu cart
        cart.setTotalPrice(totalPrice);
        cartRepository.save(cart);

        // Đảm bảo cart có danh sách cartItems mới nhất
        cart.setCartItems(updatedCartItems);

        // Chuyển đổi giỏ hàng sang DTO
        CartResponse cartDTO = modelMapper.map(cart, CartResponse.class);
        List<CartItem> cartItems = cart.getCartItems();

        List<ProductResponse> productResponses = cartItems.stream().map(item -> {
            ProductResponse productResponse = modelMapper.map(item.getProduct(), ProductResponse.class);
            productResponse.setQuantity(item.getQuantity());

            // Lọc màu sắc tương ứng
            String cartItemColor = item.getColor();
            if (cartItemColor != null && productResponse.getColors() != null) {
                productResponse.setColors(
                        productResponse.getColors().stream()
                                .filter(colors -> colors.getColor().equals(cartItemColor))
                                .toList()
                );
            } else {
                productResponse.setColors(List.of());
            }

            return productResponse;
        }).toList();

        cartDTO.setProducts(productResponses);
        return cartDTO;
    }

    @Override
    public CartResponse deleteCartByUserId(Long userId) throws ApiException {
        Cart cart = (Cart) cartRepository.findByUserId(Math.toIntExact(userId)).orElseThrow(()
                -> new ApiException("Cart is not exists"));
        deleteCartAndItems(cart.getCartId());
        return null;
    }


    @Transactional
    @Override
    public void deleteCartItemByUserId(Long userID, Long cartItemId) throws ApiException {
        CartItem cartItemToDelete = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ApiException("Cart item not found"));

        cartItemRepository.deleteByCartItemId(cartItemId);
        cartItemRepository.flush();   // Ghi thay đổi ngay xuống DB
        entityManager.clear();
        entityManager.remove(entityManager.contains(cartItemToDelete) ? cartItemToDelete : entityManager.merge(cartItemToDelete));
        entityManager.flush();
        System.out.println("Deleted cart item with ID: " + cartItemId);
    }


    public void deleteCartAndItems(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found!"));

        // Xóa toàn bộ CartItem trước
        cartItemRepository.deleteAll(cart.getCartItems());

        // Đảm bảo dữ liệu được cập nhật ngay lập tức
        cartItemRepository.flush();

        // Sau đó mới xóa Cart
        cartRepository.deleteByCartId(cartId);
        cartRepository.flush();
    }

    private Cart createCart() {
        Cart userCart = cartRepository.findCartByUserId(authUtil.loggedInUserId());
        if (userCart != null) {
            return userCart;
        }

        Cart cart = new Cart();
        cart.setTotalPrice(0.00);
        cart.setUser(authUtil.loggedInUser());
        Cart newCart = cartRepository.save(cart);
        return newCart;
    }

    @Override
    public List<CartResponse> getAllCarts() throws ApiException {
        List<Cart> carts = cartRepository.findAll();
        if (carts.size() == 0) {
            throw new ApiException("No cart exists");
        }

        List<CartResponse> cartResponses = carts.stream()
                .map(cart -> {
                    CartResponse cartResponse = modelMapper.map(cart, CartResponse.class);

                    // Map product và thumbnails
                    List<ProductResponse> products = cart.getCartItems().stream()
                            .map(cartItem -> {
                                // Map Product
                                ProductResponse productResponse = modelMapper.map(cartItem.getProduct(), ProductResponse.class);
                                productResponse.setQuantity(cartItem.getQuantity());
                                // Map danh sách hình ảnh vào thumbnails
                                List<ProductResponse.ProductImageResponse> thumbnails = cartItem.getProduct().getImages().stream()
                                        .map(image -> modelMapper.map(image, ProductResponse.ProductImageResponse.class))
                                        .collect(Collectors.toList());

                                productResponse.setThumbnails(thumbnails); // Gán vào trường thumbnails

                                return productResponse;
                            })
                            .collect(Collectors.toList());

                    cartResponse.setProducts(products);
                    return cartResponse;
                })
                .collect(Collectors.toList());

        return cartResponses;
    }

    @Override
    public CartResponse getCart(String emailId, Long cartId) throws ApiException {
        Cart cart = cartRepository.findCartByEmailAndCartId(emailId, cartId);
        if (cart == null) {
            throw new ApiException("Cannot find Cart");
        }
        CartResponse cartResponse = modelMapper.map(cart, CartResponse.class);

        List<ProductResponse> products = cart.getCartItems().stream()
                .map(cartItem -> {
                    // Map Product
                    ProductResponse productResponse = modelMapper.map(cartItem.getProduct(), ProductResponse.class);
                    productResponse.setQuantity(cartItem.getQuantity());

                    // Lọc màu sắc dựa trên CartItem.color
                    String cartItemColor = cartItem.getColor();
                    if (cartItemColor != null && productResponse.getColors() != null) {
                        ProductResponse.ProductColorResponse foundColor = productResponse.getColors().stream()
                                .filter(color -> color.getColor().equals(cartItemColor))
                                .findFirst()
                                .orElse(null);

                        if (foundColor != null) {
                            // Nếu tìm thấy, tạo một danh sách mới chỉ chứa màu sắc đã chọn
                            List<ProductResponse.ProductColorResponse> selectedColors = List.of(foundColor);
                            productResponse.setColors(selectedColors);
                        } else {
                            // Nếu không tìm thấy, bạn có thể xử lý lỗi hoặc để danh sách màu sắc trống
                            productResponse.setColors(List.of());
                        }
                    } else {
                        // Nếu cartItemColor là null hoặc productResponse.getColors() là null, để danh sách màu sắc trống
                        productResponse.setColors(List.of());
                    }

                    // Map danh sách hình ảnh vào thumbnails
                    List<ProductResponse.ProductImageResponse> thumbnails = cartItem.getProduct().getImages().stream()
                            .map(image -> modelMapper.map(image, ProductResponse.ProductImageResponse.class))
                            .collect(Collectors.toList());

                    productResponse.setThumbnails(thumbnails);
                    ProductDetails productDetails = productDetailsRepository.findByProductId(cartItem.getProduct().getId());
                    ProductDetailsResponse productDetailsResponse = productMapper.toProductDetailsResponse(productDetails);
                    productResponse.setDetails(productDetailsResponse);
                    return productResponse;
                })
                .collect(Collectors.toList());

        cartResponse.setProducts(products);
        return cartResponse;
    }

    @Transactional
    @Override
    public CartResponse updateProductQuantityInCart(Long productId, int quantityChange, String color) throws ApiException {
        // Lấy email của người dùng đã đăng nhập
        String emailId = authUtil.loggedInEmail();

        // Lấy giỏ hàng của người dùng theo email
        Cart userCart = cartRepository.findCartByEmail(emailId);
        System.out.println(userCart);
        Long cartId = userCart.getCartId();

        // Tìm giỏ hàng trong cơ sở dữ liệu
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ApiException("Cart is not exists"));

        // Tìm sản phẩm từ ID và kiểm tra tồn tại
        Product product = productRepository.findById(Math.toIntExact(productId))
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

//        // Kiểm tra tồn kho sản phẩm
//        InventoryItem inventory = inventoryRepository.findByProductId(Math.toIntExact(productId));
//        if (inventory == null || inventory.getQuantity() == 0) {
//            throw new AppException(ErrorCode.OutofStock);
//        }

        // Kiểm tra số lượng sản phẩm có đủ không
//        if (inventory.getQuantity() < quantityChange) {
//            throw new ApiException("Please, make an order of the " + product.getName()
//                    + " less than or equal to the quantity " + inventory.getQuantity() + ".");
//        }

        // Tìm sản phẩm trong giỏ hàng theo productId, cartId và color
        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartIdAndColor(cartId, productId, color);

        if (cartItem == null) {
            throw new ApiException("Product " + product.getName() + " with color " + color + " not available in the cart!!!");
        }

        // Tính số lượng mới sau khi thay đổi
        int newQuantity = quantityChange;
        if (newQuantity < 0) {
            throw new ApiException("Quantity cannot be less than zero.");
        }
        cartItem.setQuantity(newQuantity);

        // Cập nhật giá và chiết khấu của sản phẩm trong giỏ

        if (product.getSpecialPrice() != null) {
            cartItem.setProductPrice(product.getSpecialPrice());
        } else {
            // Nếu SpecialPrice bị null, bạn có thể gán giá trị mặc định hoặc dùng regularPrice
            cartItem.setProductPrice(product.getPrice()); // hoặc 0 tùy theo logic của bạn
        }

        cartItem.setDiscount(product.getDiscount() != null ? product.getDiscount() : 0.0);
        // Cập nhật tổng giá giỏ hàng
        // cart.setTotalPrice(cart.getTotalPrice() + (cartItem.getProductPrice() * quantityChange));
        cart.setTotalPrice((cartItem.getProductPrice() * quantityChange));
        log.info("Cart:" + cart);
        // Lưu giỏ hàng
        cartRepository.save(cart);

        // Lưu cập nhật cho sản phẩm trong giỏ
        CartItem updatedItem = cartItemRepository.save(cartItem);

        // Nếu số lượng sản phẩm bằng 0, xóa sản phẩm khỏi giỏ
        if (updatedItem.getQuantity() == 0) {
            cartItemRepository.deleteById(updatedItem.getCartItemId());
            cart.setTotalPrice(cart.getTotalPrice() - (cartItem.getProductPrice() * quantityChange));
        }

        // Map Cart thành CartResponse
        CartResponse cartDTO = modelMapper.map(cart, CartResponse.class);

        // Lấy danh sách các sản phẩm trong giỏ
        List<CartItem> cartItems = cart.getCartItems();

        // Map từng sản phẩm thành ProductResponse
        Stream<ProductResponse> productStream = cartItems.stream().map(item -> {
            ProductResponse prd = modelMapper.map(item.getProduct(), ProductResponse.class);

            prd.setQuantity(item.getQuantity());
            // Tìm ProductColorResponse tương ứng với item.getColor()
            String cartItemColor = item.getColor();
            if (cartItemColor != null && prd.getColors() != null) {
                ProductResponse.ProductColorResponse foundColor = prd.getColors().stream()
                        .filter(colors -> colors.getColor().equals(cartItemColor))
                        .findFirst()
                        .orElse(null);

                if (foundColor != null) {
                    // Nếu tìm thấy, tạo một danh sách mới chỉ chứa màu sắc đã chọn
                    List<ProductResponse.ProductColorResponse> selectedColors = List.of(foundColor);
                    prd.setColors(selectedColors);
                } else {
                    // Nếu không tìm thấy, bạn có thể xử lý lỗi hoặc để danh sách màu sắc trống
                    prd.setColors(List.of()); // Hoặc xử lý lỗi theo yêu cầu của bạn
                }
            } else {
                // Nếu cartItemColor là null hoặc prd.getColors() là null, để danh sách màu sắc trống
                prd.setColors(List.of());
            }

            ProductDetails productDetails = productDetailsRepository.findByProductId(item.getProduct().getId());
            ProductDetailsResponse productDetailsResponse = productMapper.toProductDetailsResponse(productDetails);
            prd.setDetails(productDetailsResponse);
            // Lấy hình ảnh của sản phẩm
            List<ProductResponse.ProductImageResponse> thumbnails = item.getProduct().getImages().stream()
                    .map(image -> modelMapper.map(image, ProductResponse.ProductImageResponse.class))
                    .collect(Collectors.toList());

            prd.setThumbnails(thumbnails);

            return prd;
        });

        // Thiết lập danh sách sản phẩm trong giỏ hàng
        cartDTO.setProducts(productStream.toList());

        // Log thông tin và trả về kết quả
        log.info("Successfully updated product quantity in the cart for user: {}", emailId);
        return cartDTO;
    }

    @Override
    public List<CartItemResponse> getCartItemsByUserId(Long userId) {
        return cartItemRepository.findCartItemByUserId(userId)
                .stream()
                .map(this::mapCartItemToCartItemResponse)
                .collect(Collectors.toList());
    }

    private CartItemResponse mapCartItemToCartItemResponse(CartItem cartItem) {
        CartItemResponse response = new CartItemResponse();
        response.setCartItemId(cartItem.getCartItemId());
        response.setQuantity(cartItem.getQuantity());
        response.setDiscount(cartItem.getDiscount());
        response.setProductPrice(cartItem.getProductPrice());
        response.setColor(cartItem.getColor());
        // Map Cart
        if (cartItem.getCart() != null) {
            CartResponse cartResponse = new CartResponse();
            cartResponse.setCartId(cartItem.getCart().getCartId());
            cartResponse.setUserId((long) cartItem.getCart().getUser().getId());
            response.setCart(cartResponse);
        }

        // Map Product
        if (cartItem.getProduct() != null) {
            ProductResponse productResponse = new ProductResponse();
            productResponse.setId(cartItem.getProduct().getId());
            productResponse.setName(cartItem.getProduct().getName());
            productResponse.setPrice(cartItem.getProduct().getPrice());
            productResponse.setDiscount(cartItem.getProduct().getDiscount());

            // Map images to thumbnails
            if (cartItem.getProduct().getImages() != null && !cartItem.getProduct().getImages().isEmpty()) {
                productResponse.setThumbnails(cartItem.getProduct().getImages().stream()
                        .map(image -> {
                            ProductResponse.ProductImageResponse imageResponse = new ProductResponse.ProductImageResponse();
                            imageResponse.setId(image.getId());
                            imageResponse.setImageUrl(image.getImageUrl());
                            return imageResponse;
                        })
                        .collect(Collectors.toList()));
            }

            // Handle null colors
            if (cartItem.getProduct().getColors() != null && !cartItem.getProduct().getColors().isEmpty()) {
                productResponse.setColors(cartItem.getProduct().getColors().stream()
                        .map(color -> {
                            ProductResponse.ProductColorResponse colorResponse = new ProductResponse.ProductColorResponse();
                            colorResponse.setId(color.getId());
                            colorResponse.setColor(color.getColor());
                            return colorResponse;
                        })
                        .collect(Collectors.toList()));
            }
            response.setProduct(productResponse);
        }

        return response;
    }
}