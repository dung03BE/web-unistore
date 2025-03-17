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
import com.dung.UniStore.repository.ICartItemRepository;
import com.dung.UniStore.repository.ICartRepository;
import com.dung.UniStore.repository.IOrderRepository;
import com.dung.UniStore.repository.IUserRepository;
import com.dung.UniStore.specification.OrderSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService implements IOrderService{
    private final IOrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final IUserRepository userRepository;
    private final ICartRepository cartRepository;
    private final ICartItemRepository cartItemRepository;
    private final IOrderDetailService orderDetailService;
    @Autowired
    private EmailService emailService;
    @Override
    public List<OrderResponse> getAllOrders(OrderFilterForm form) {
        Specification<Order> where = OrderSpecification.buildWhere(form);
        return orderRepository.findAll(where).stream().map(orderMapper::toOrderResponse).toList() ;
    }

    @Override
    public OrderResponse createOrder(OrderCreationRequest request) throws Exception {
        User existingUser =userRepository.findById(request.getUserId()).orElseThrow(
                ()->new AppException(ErrorCode.USER_NOT_EXISTED)
        );
        Order order = orderMapper.toOrder(request);
//        order.setUser();
        order.setOrderDate(new Date());//time now
        order.setStatus(Status.pending);
        LocalDate shippingDate =request.getShippingDate()==null?
                LocalDate.now():request.getShippingDate();
        //shippingDate phải là từ ngày hôm nay
        if(shippingDate.isBefore(LocalDate.now()))
        {
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
    public OrderResponse checkout(OrderCreationRequest request) throws Exception {

        User existingUser =userRepository.findById(request.getUserId()).orElseThrow(
                ()->new AppException(ErrorCode.USER_NOT_EXISTED)
        );
        Cart cart = (Cart) cartRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Giỏ hàng trống!"));
        Order order = orderMapper.toOrder(request);
        order.setUser(existingUser);
        order.setOrderDate(new Date());//time now
        order.setStatus(Status.pending);
        LocalDate shippingDate =request.getShippingDate()==null?
                LocalDate.now():request.getShippingDate();
        //shippingDate phải là từ ngày hôm nay
        if(shippingDate.isBefore(LocalDate.now()))
        {
            throw new Exception("Data must be at least today");
        }
        order.setShippingDate(shippingDate);
        order.setActive(true);
        order.setPaymentMethod(request.getPaymentMethod());
        order.setNote(request.getNote());
        order = orderRepository.save(order);

        BigDecimal totalAmount = orderDetailService.createOrderDetails(order.getId(), cart.getCartId());
        order.setTotalMoney(totalAmount);
        orderRepository.save(order);
        deleteCartAndItems(cart.getCartId());
        log.info("Deleted Cart with cartId: {}", cart.getCartId());
        OrderConfirmationMessage message = new OrderConfirmationMessage();
        message.setId(order.getId());
        message.setCustomerEmail(order.getEmail());

        // Gửi email xác nhận
        emailService.sendOrderConfirmationEmail(message);
        OrderResponse orderResponse = orderMapper.toOrderResponse(order);
        return orderResponse;
    }

    @Override
    public List<OrderResponse> getOrderByUserId(int userId) {
        List<Order> orders = orderRepository.findAllByUserId(userId); // Không cần ép kiểu
        List<OrderResponse> orderResponses = orders.stream()
                .map(orderMapper::toOrderResponse) // Chuyển đổi từng Order thành OrderResponse
                .collect(Collectors.toList());
        return orderResponses;
    }

    @Override
    public OrderResponse updateStatusOrder(int id, OrderCreationRequest request) {
        Order order = orderRepository.findById(id)
                .orElseThrow(()-> new AppException(ErrorCode.ORDER_NOT_EXISTS));
        order.setStatus(request.getStatus());
        orderRepository.save(order);
        return orderMapper.toOrderResponse(order);
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
}
