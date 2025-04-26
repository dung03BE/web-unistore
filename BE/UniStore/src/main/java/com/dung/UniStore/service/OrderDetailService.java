package com.dung.UniStore.service;

import com.dung.UniStore.dto.request.OrderDetailCreationRequest;
import com.dung.UniStore.dto.response.OrderDetailsResponse;
import com.dung.UniStore.entity.*;
import com.dung.UniStore.exception.ApiException;
import com.dung.UniStore.exception.AppException;
import com.dung.UniStore.exception.ErrorCode;
import com.dung.UniStore.mapper.OrderDetailMapper;
import com.dung.UniStore.repository.*;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class OrderDetailService implements IOrderDetailService{
    private final IOrderDetailRepository orderDetailRepository;
    private final IOrderRepository orderRepository;
    private final IProductRepository productRepository;
    private final OrderDetailMapper orderDetailMapper;
    private final RedisTemplate<String,Object> redisTemplate;
//    private final InventoryRepository inventoryRepository;
    private final ICartItemRepository cartItemRepository;
    private final IInventoryRepository iinventoryRepository;
    private final IProductColorRepo productColorRepo;
    @Autowired
    private RedissonClient redissonClient;

    @Override
    public BigDecimal createOrderDetails(int orderId, Long cartId) throws ApiException {
        List<CartItem> cartItems = cartItemRepository.findByCartCartId(cartId);
        if (cartItems.isEmpty()) {
            throw new ApiException("Giỏ hàng trống!");
        }
        BigDecimal totalAmount = BigDecimal.ZERO;
        Optional<Order> order = orderRepository.findById(orderId);
        for(CartItem cartItem: cartItems)
        {
            // Trừ số lượng trong kho
            // Lấy ProductColor từ productId và color (String)
            ProductColor productColor = (ProductColor) productColorRepo
                    .findByProductIdAndColor(cartItem.getProduct().getId(), cartItem.getColor())
                    .orElseThrow(() -> new ApiException("Không tìm thấy màu sản phẩm phù hợp"));

            // Lấy inventory theo product + color
            Inventory inventory = (Inventory) iinventoryRepository
                    .findByProductIdAndProductColorId(cartItem.getProduct().getId(), productColor.getId())
                    .orElseThrow(() -> new ApiException("Kho sản phẩm không tồn tại"));

            // Kiểm tra số lượng tồn kho
            if (inventory.getQuantity() < cartItem.getQuantity()) {
                throw new ApiException("Sản phẩm \"" + cartItem.getProduct().getName() +
                        "\" (màu: " + productColor.getColor() + ") không đủ số lượng trong kho.");
            }

            // Trừ kho
            inventory.setQuantity(inventory.getQuantity() - cartItem.getQuantity());
            iinventoryRepository.save(inventory);

            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order.get());
            orderDetail.setProduct(cartItem.getProduct());
            orderDetail.setQuantity(cartItem.getQuantity());
            orderDetail.setColor(cartItem.getColor());
            orderDetail.setPrice(BigDecimal.valueOf(cartItem.getProductPrice()));
            BigDecimal price = BigDecimal.valueOf(cartItem.getProductPrice());
            BigDecimal itemTotal = price.multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);
            orderDetailRepository.save(orderDetail);
        }
        return totalAmount;
    }

//    @Override
//    public OrderDetailsResponse createOrderDetail(OrderDetailCreationRequest request) {
//        // Lấy thông tin Order và Product
//        Product existingProduct = productRepository.findById(request.getProductId()).orElseThrow(
//                ()-> new AppException(ErrorCode.PRODUCT_NOT_EXISTED)
//        );
//        Order order = orderRepository.findById(request.getOrderId())
//                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTS));
//        InventoryItem inventoryItem = inventoryRepository.findByProductId(request.getProductId());
//
//
//        // Tạo khóa duy nhất cho sản phẩm
//        RLock lock = redissonClient.getLock("lock:product:" + request.getProductId());
//
//        System.out.println("Hàng kho: "+ inventoryItem.getQuantity());
//        System.out.println("Hàng yêu cầu: "+ request.getQuantity());
//        try {
////            10: Thời gian tối đa (10 giây) để thử lấy khóa.
////            30: Thời gian giữ khóa (30 giây) sau khi đã có khóa.
//            if (lock.tryLock(10, 30, TimeUnit.SECONDS)) {
//                System.out.println("Lock acquired!");
//
//                try {
//                    // Kiểm tra số lượng sản phẩm có đủ để đặt hàng không
//                    if (inventoryItem.getQuantity() < request.getQuantity()) {
//                        System.out.println("Out of stock!!!");
//                        throw new AppException(ErrorCode.OutofStock);
//                    }
//                    // Tạo OrderDetail
//                    OrderDetail orderDetail = OrderDetail.builder()
//                            .order(order)
//                            .product(existingProduct)
//                            .quantity(request.getQuantity())
//                            .color(request.getColor())
//                            .price(request.getPrice())
//                            .build();
//
//                    // Lưu OrderDetail
//                    orderDetailRepository.save(orderDetail);
//
//                    // Giảm số lượng sản phẩm trong kho
//                    inventoryItem.setQuantity(inventoryItem.getQuantity() - request.getQuantity());
//                    inventoryRepository.save(inventoryItem); // Cập nhật số lượng sản phẩm
//
//                    // Chuyển đổi thành DTO để trả về
//                    return orderDetailMapper.toOrderDetailsResponse(orderDetail);
//                } finally {
//                    // Giải phóng lock
//                    lock.unlock();
//                }
//            } else {
//                throw new RuntimeException("Vui lòng thử lại sau khi sản phẩm đang được xử lý.");
//            }
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt(); // Khôi phục trạng thái gián đoạn
//            throw new RuntimeException("Có lỗi xảy ra khi đặt hàng", e);
//        }
//    }
}

