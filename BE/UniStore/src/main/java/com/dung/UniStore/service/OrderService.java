package com.dung.UniStore.service;

import com.dung.UniStore.dto.request.OrderConfirmationMessage;
import com.dung.UniStore.dto.request.OrderCreationRequest;
import com.dung.UniStore.dto.response.OrderResponse;
import com.dung.UniStore.entity.*;
import com.dung.UniStore.exception.ApiException;
import com.dung.UniStore.exception.AppException;
import com.dung.UniStore.exception.ErrorCode;
import com.dung.UniStore.form.OrderFilterForm;
import com.dung.UniStore.mapper.OrderMapper;
import com.dung.UniStore.repository.*;
import com.dung.UniStore.specification.OrderSpecification;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService implements IOrderService {
    private final IOrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final IUserRepository userRepository;
    private final ICartRepository cartRepository;
    private final ICartItemRepository cartItemRepository;
    private final IOrderDetailService orderDetailService;
    private final VNPayService vnPayService;
    private final PaymentRepository paymentRepository;
    private final ICounponRepository counponRepository;
    private final CouponUserUsageRepository couponUserUsageRepository;
    @Autowired
    private EmailService emailService;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_EMPLOYEE')")
    @Override
    public List<OrderResponse> getAllOrders(OrderFilterForm form) {
        Specification<Order> where = OrderSpecification.buildWhere(form);
        return orderRepository.findAll(where).stream()
                .map(orderMapper::toOrderResponse)
                .sorted(Comparator.comparing(OrderResponse::getOrderDate).reversed()) // Sắp xếp giảm dần theo ngày
                .toList();
    }

    @Override
    public OrderResponse createOrder(OrderCreationRequest request) throws Exception {
        User existingUser = userRepository.findById(request.getUserId()).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );
        Order order = orderMapper.toOrder(request);
//        order.setUser();
        order.setOrderDate(new Date());//time now
        order.setStatus(Status.pending);
        LocalDate shippingDate = request.getShippingDate() == null ?
                LocalDate.now() : request.getShippingDate();
        //shippingDate phải là từ ngày hôm nay
        if (shippingDate.isBefore(LocalDate.now())) {
            throw new Exception("Data must be at least today");
        }
        order.setShippingDate(shippingDate);
        order.setActive(true);
        orderRepository.save(order);
        OrderResponse orderResponse = orderMapper.toOrderResponse(order);
        OrderConfirmationMessage message = new OrderConfirmationMessage();
        message.setId(order.getId());
        message.setCustomerEmail(order.getEmail());

        // Gửi email xác nhận
        emailService.sendOrderConfirmationEmail(message);

        return orderResponse;
    }


    @Override
    public List<OrderResponse> getOrderByUserId(int userId) {
        List<Order> orders = orderRepository.findAllByUserIdOrderByOrderDateDesc(userId); // đã sắp xếp mới nhất
        return orders.stream()
                .map(order -> {
                    OrderResponse orderResponse = orderMapper.toOrderResponse(order);
                    // Lấy trạng thái thanh toán từ bảng Payments
                    Payment payment = paymentRepository.findFirstByOrderId(order.getId());
                    if (payment != null) {
                        orderResponse.setPaymentStatus(payment.getStatus()); // Cập nhật trạng thái thanh toán
                    }
                    return orderResponse;
                })
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_EMPLOYEE')")
    @Override
    public OrderResponse updateStatusOrder(int id, OrderCreationRequest request) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTS));
        order.setStatus(request.getStatus());
        orderRepository.save(order);
        return orderMapper.toOrderResponse(order);
    }

    @Transactional(rollbackFor = {Exception.class})
    @Override
    public String checkoutVnpay(OrderCreationRequest request, Long userId, HttpServletRequest httpRequest) throws Exception {
        Counpons coupon = null;
        User existingUser = userRepository.findById(Math.toIntExact(userId)).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );
        Cart cart = (Cart) cartRepository.findByUserId(Math.toIntExact(userId))
                .orElseThrow(() -> new ApiException("Giỏ hàng trống!"));

        // Kiểm tra mã coupon
        BigDecimal discountAmount = BigDecimal.ZERO;
        if (request.getCouponCode() != null && !request.getCouponCode().isEmpty()) {
            List<Counpons> coupons = counponRepository.findByCodeForUser(request.getCouponCode(), Long.valueOf(request.getUserId()));
            if (coupons == null || coupons.isEmpty()) {
                throw new ApiException("Không tìm thấy coupon!");
            }
// Lấy coupon đầu tiên (ưu tiên coupon cho user nếu có)
            coupon = coupons.get(0);

            // Kiểm tra xem coupon có còn hiệu lực không
            if (coupon.getStartDate().isAfter(LocalDateTime.now()) || coupon.getEndDate().isBefore(LocalDateTime.now())) {
                throw new ApiException("Mã giảm giá đã hết hạn!");
            }

            if (coupon.getUser() == null) {
                // Kiểm tra số lần sử dụng của coupon
                if (coupon.getUsedCount() >= coupon.getUsageLimit()) {
                    throw new AppException(ErrorCode.LIMITED_USED);
                }
                if (coupon.getUserLimit() > 0 ) {
                    // Nếu coupon là công cộng (userId == null), kiểm tra bảng CouponUserUsage
                    boolean hasUsed = couponUserUsageRepository.existsByCouponIdAndUserId(coupon.getId(), existingUser.getId());
                    if (hasUsed) {
                        throw new AppException(ErrorCode.COUPON_IS_USED);
                    }
                    // Áp dụng giảm giá
                    if (coupon.getDiscountType() == Counpons.DiscountType.percentage) {

                        BigDecimal totalPrice = BigDecimal.valueOf(cart.getTotalPrice());
                        discountAmount = totalPrice.multiply(coupon.getDiscountValue());
                    } else if (coupon.getDiscountType() == Counpons.DiscountType.fixed_amount) {
                        discountAmount = coupon.getDiscountValue(); // Giảm theo số tiền cố định
                    }

                    // Cập nhật số lần sử dụng của coupon
                    coupon.setUsedCount(coupon.getUsedCount() + 1);
                }
            } else {
                coupon = counponRepository.findByCodeAndUserId(request.getCouponCode(), request.getUserId());
                if (coupon.getStatus() != Status.active) {
                    throw new ApiException("Mã giảm giá không còn hiệu lực!");
                }
                // Kiểm tra xem người dùng đã sử dụng coupon chưa
                boolean hasUsed = couponUserUsageRepository.existsByCouponIdAndUserId(coupon.getId(), existingUser.getId());
                if (hasUsed) {
                    throw new ApiException("Bạn đã sử dụng mã giảm giá này rồi!");
                }
                // Áp dụng giảm giá
                if (coupon.getDiscountType() == Counpons.DiscountType.percentage) {

                    BigDecimal totalPrice = BigDecimal.valueOf(cart.getTotalPrice());
                    discountAmount = totalPrice.multiply(coupon.getDiscountValue());
                } else if (coupon.getDiscountType() == Counpons.DiscountType.fixed_amount) {
                    discountAmount = coupon.getDiscountValue(); // Giảm theo số tiền cố định
                }
            }
            counponRepository.save(coupon);

//            // Lưu vào bảng CouponUserUsage nếu coupon là công cộng và người dùng chưa sử dụng
            if (coupon.getUser() == null) {
                CouponUserUsage couponUserUsage = new CouponUserUsage();
                couponUserUsage.setCouponId(coupon.getId());
                couponUserUsage.setUserId(Long.valueOf(existingUser.getId()));
                couponUserUsageRepository.save(couponUserUsage);
            }
        }
        Order order = orderMapper.toOrder(request);
        order.setUser(existingUser);
        order.setOrderDate(new Date()); // Time now
        order.setStatus(Status.pending);

        LocalDate shippingDate = request.getShippingDate() == null ? LocalDate.now() : request.getShippingDate();
        if (shippingDate.isBefore(LocalDate.now())) {
            throw new Exception("Shipping date must be at least today");
        }
        order.setShippingDate(shippingDate);
        order.setActive(true);
        order.setPaymentMethod(request.getPaymentMethod());
        order.setNote(request.getNote());
        order = orderRepository.save(order);

//        BigDecimal totalAmount = orderDetailService.createOrderDetails(order.getId(), cart.getCartId());
//        order.setTotalMoney(totalAmount);
//        orderRepository.save(order);

        // Tính toán tổng tiền với giảm giá
        BigDecimal totalAmount = orderDetailService.createOrderDetails(order.getId(), cart.getCartId());
        totalAmount = totalAmount.subtract(discountAmount); // Trừ đi giảm giá nếu có
        order.setTotalMoney(totalAmount);
        orderRepository.save(order);
        if (coupon != null && coupon.getUser() != null) {
            coupon.setStatus(Status.expired);
            counponRepository.save(coupon);
        }
        // Kiểm tra thời gian thanh toán và trạng thái thanh toán
        String baseUrl = httpRequest.getScheme() + "://" + httpRequest.getServerName() + ":" + httpRequest.getServerPort();
        String paymentUrl = vnPayService.createOrder(totalAmount.intValue(), String.valueOf(order.getId()), baseUrl);

        // Trả về URL thanh toán để frontend redirect
        return paymentUrl;
    }



    @Transactional(rollbackFor = {Exception.class})
    @Override
    public OrderResponse checkout(OrderCreationRequest request) throws Exception {
        Counpons coupon = null;
        // Kiểm tra người dùng
        User existingUser = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // Kiểm tra giỏ hàng
        Cart cart = (Cart) cartRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new ApiException("Giỏ hàng trống!"));

        // Kiểm tra mã coupon
        BigDecimal discountAmount = BigDecimal.ZERO;
        if (request.getCouponCode() != null && !request.getCouponCode().isEmpty()) {
            List<Counpons> coupons = counponRepository.findByCodeForUser(request.getCouponCode(), Long.valueOf(request.getUserId()));
            if (coupons == null || coupons.isEmpty()) {
                throw new ApiException("Không tìm thấy coupon!");
            }
// Lấy coupon đầu tiên (ưu tiên coupon cho user nếu có)
            coupon = coupons.get(0);
            // Kiểm tra xem coupon có còn hiệu lực không
            if (coupon.getStartDate().isAfter(LocalDateTime.now()) || coupon.getEndDate().isBefore(LocalDateTime.now())) {
                throw new ApiException("Mã giảm giá đã hết hạn!");
            }

            if (coupon.getUser() == null) {
                // Kiểm tra số lần sử dụng của coupon
                if (coupon.getUsedCount() >= coupon.getUsageLimit()) {
                    throw new AppException(ErrorCode.LIMITED_USED);
                }
                if (coupon.getUserLimit() > 0 ) {
                    // Nếu coupon là công cộng (userId == null), kiểm tra bảng CouponUserUsage
                    boolean hasUsed = couponUserUsageRepository.existsByCouponIdAndUserId(coupon.getId(), existingUser.getId());
                    if (hasUsed) {
                        throw new AppException(ErrorCode.COUPON_IS_USED);
                    }
                    // Áp dụng giảm giá
                    if (coupon.getDiscountType() == Counpons.DiscountType.percentage) {

                        BigDecimal totalPrice = BigDecimal.valueOf(cart.getTotalPrice());
                        discountAmount = totalPrice.multiply(coupon.getDiscountValue());
                    } else if (coupon.getDiscountType() == Counpons.DiscountType.fixed_amount) {
                        discountAmount = coupon.getDiscountValue(); // Giảm theo số tiền cố định
                    }

                    // Cập nhật số lần sử dụng của coupon
                    coupon.setUsedCount(coupon.getUsedCount() + 1);
                }
            } else {
                coupon = counponRepository.findByCodeAndUserId(request.getCouponCode(), request.getUserId());
                if (coupon.getStatus() != Status.active) {
                    throw new ApiException("Mã giảm giá không còn hiệu lực!");
                }
                // Kiểm tra xem người dùng đã sử dụng coupon chưa
                boolean hasUsed = couponUserUsageRepository.existsByCouponIdAndUserId(coupon.getId(), existingUser.getId());
                if (hasUsed) {
                    throw new ApiException("Bạn đã sử dụng mã giảm giá này rồi!");
                }
                // Áp dụng giảm giá
                if (coupon.getDiscountType() == Counpons.DiscountType.percentage) {

                    BigDecimal totalPrice = BigDecimal.valueOf(cart.getTotalPrice());
                    discountAmount = totalPrice.multiply(coupon.getDiscountValue());
                } else if (coupon.getDiscountType() == Counpons.DiscountType.fixed_amount) {
                    discountAmount = coupon.getDiscountValue(); // Giảm theo số tiền cố định
                }
            }
            counponRepository.save(coupon);

//            // Lưu vào bảng CouponUserUsage nếu coupon là công cộng và người dùng chưa sử dụng
            if (coupon.getUser() == null) {
                CouponUserUsage couponUserUsage = new CouponUserUsage();
                couponUserUsage.setCouponId(coupon.getId());
                couponUserUsage.setUserId(Long.valueOf(existingUser.getId()));
                couponUserUsageRepository.save(couponUserUsage);
            }
        }

        // Tạo đơn hàng
        Order order = orderMapper.toOrder(request);
        order.setUser(existingUser);
        order.setOrderDate(new

                Date());
        order.setStatus(Status.pending);

        LocalDate shippingDate = request.getShippingDate() == null ? LocalDate.now() : request.getShippingDate();
        if (shippingDate.isBefore(LocalDate.now())) {
            throw new Exception("Ngày giao hàng phải là ngày hôm nay hoặc sau đó.");
        }
        order.setShippingDate(shippingDate);
        order.setActive(true);
        order.setPaymentMethod(request.getPaymentMethod());
        order.setNote(request.getNote());
        order = orderRepository.save(order);

        // Tính toán tổng tiền với giảm giá
        BigDecimal totalAmount = orderDetailService.createOrderDetails(order.getId(), cart.getCartId());
        totalAmount = totalAmount.subtract(discountAmount); // Trừ đi giảm giá nếu có
        order.setTotalMoney(totalAmount);
        orderRepository.save(order);

        // Xóa giỏ hàng sau khi đặt
        deleteCartAndItems(cart.getCartId());

        // Tạo và lưu thanh toán
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(totalAmount);
        payment.setPaymentMethod(PaymentMethod.COD);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setPaymentTime(LocalDateTime.now());
        paymentRepository.save(payment);

        log.info("Deleted Cart with cartId: {}", cart.getCartId());

        // Gửi email xác nhận đơn hàng
        OrderConfirmationMessage message = new OrderConfirmationMessage();
        message.setId(order.getId());
        message.setCustomerEmail(order.getEmail());
        emailService.sendOrderConfirmationEmail(message);
        if (coupon != null && coupon.getUser() != null) {
            coupon.setStatus(Status.expired);
            counponRepository.save(coupon);
        }

        // Trả về phản hồi đơn hàng
        OrderResponse orderResponse = orderMapper.toOrderResponse(order);
        return orderResponse;
    }


    public void deleteCartAndItems(Long cartId) {
//        Cart cart = cartRepository.findById(cartId)
//                .orElseThrow(() -> new RuntimeException("Cart not found!"));
//
//        // Xóa toàn bộ CartItem trước
//        cartItemRepository.deleteAll(cart.getCartItems());
//
//        // Đảm bảo dữ liệu được cập nhật ngay lập tức
//        cartItemRepository.flush();
//
//        // Sau đó mới xóa Cart
//        cartRepository.deleteByCartId(cartId);
//        cartRepository.flush();
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found!"));

        // 1. Xóa từng cartItem khỏi cart + xóa liên kết ngược
        for (CartItem item : new ArrayList<>(cart.getCartItems())) {
            item.setCart(null); // Bắt buộc để tránh vòng lặp và detached entity
        }

        // 2. Xóa tất cả cartItems bằng clear() → orphanRemoval sẽ xóa khỏi DB
        cart.getCartItems().clear();

        // 3. Xóa cart luôn
        cartRepository.delete(cart);
    }
}
