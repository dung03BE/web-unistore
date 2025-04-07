package com.dung.UniStore.controller;

import com.dung.UniStore.dto.request.OrderConfirmationMessage;
import com.dung.UniStore.entity.*;
import com.dung.UniStore.repository.ICartItemRepository;
import com.dung.UniStore.repository.ICartRepository;
import com.dung.UniStore.repository.IOrderRepository;
import com.dung.UniStore.repository.PaymentRepository;
import com.dung.UniStore.service.EmailService;
import com.dung.UniStore.service.OrderService;
import com.dung.UniStore.service.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("")
@RequiredArgsConstructor
public class VNPayController {

    private final VNPayService vnPayService;
    private final PaymentRepository paymentRepository;
    private final IOrderRepository orderRepository;
    private final OrderService orderService;
    private final ICartRepository cartRepository;
    private final EmailService emailService;
    @GetMapping("")
    public String home() {
        return "index";
    }

    @PostMapping("/submitOrder")
    public String submidOrder(@RequestParam("amount") int orderTotal,
                              @RequestParam("orderInfo") String orderInfo,
                              HttpServletRequest request) {
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        String vnpayUrl = vnPayService.createOrder(orderTotal, orderInfo, baseUrl);
        return "redirect:" + vnpayUrl;
    }

    @GetMapping("/vnpay-payment")
    public void vnpayReturn(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int paymentStatus = vnPayService.orderReturn(request);
        String responseCode = request.getParameter("vnp_ResponseCode");
        String orderInfo = request.getParameter("vnp_OrderInfo");
        String redirectUrl = "http://localhost:3001/payment-result";
        try {
            Long orderId = Long.parseLong(orderInfo);
            Optional<Order> orderOpt = orderRepository.findById(Math.toIntExact(orderId));

            if (orderOpt.isPresent()) {
                Order order = orderOpt.get();

                if ("24".equals(responseCode)) {
                    // Giao dịch bị hủy bỏ bởi khách hàng
                    order.setStatus(Status.cancelled);
                    orderRepository.save(order);
                    // Có thể xóa giỏ hàng ở đây nếu cần
                    Cart cart = (Cart) cartRepository.findByUserId(order.getUser().getId())
                            .orElseThrow(() -> new RuntimeException("Cart not found!"));
                    orderService.deleteCartAndItems(cart.getCartId()); // Xóa giỏ hàng
                    response.sendRedirect(redirectUrl + "?status=cancelled&orderId=" + orderId + "&message=user_cancelled");
                    return;
                } else if (paymentStatus == 1) {
                    // Giao dịch thành công (phần code hiện tại của bạn)
                    String transactionId = request.getParameter("vnp_TransactionNo");
                    String amountStr = request.getParameter("vnp_Amount");
                    BigDecimal amount = new BigDecimal(amountStr).divide(BigDecimal.valueOf(100));
                    PaymentStatus status = PaymentStatus.SUCCESS;
                    Payment payment = new Payment();
                    payment.setOrder(order);
                    payment.setTransactionId(transactionId);
                    payment.setPaymentMethod(PaymentMethod.VNPAY);
                    payment.setStatus(status);
                    payment.setAmount(amount);
                    payment.setPaymentTime(LocalDateTime.now());
                    paymentRepository.save(payment);
                    Cart cart = (Cart) cartRepository.findByUserId(order.getUser().getId())
                            .orElseThrow(() -> new RuntimeException("Cart not found!"));
                    orderService.deleteCartAndItems(cart.getCartId()); // Xóa giỏ hàng
                    order.setStatus(Status.processing);
                    orderRepository.save(order);
                    OrderConfirmationMessage message = new OrderConfirmationMessage();
                    message.setId(order.getId());
                    message.setCustomerEmail(order.getEmail());
                    emailService.sendOrderConfirmationEmail(message);
                    response.sendRedirect(String.format("%s?status=success&orderId=%d&amount=%s",
                            redirectUrl, order.getId(), amount.toString()));
                    return;
                } else {
                    // Giao dịch thất bại vì lý do khác
                    order.setStatus(Status.cancelled); // Hoặc trạng thái phù hợp
                    orderRepository.save(order);
                    // Xóa giỏ hàng khi thanh toán thất bại
                    Cart cart = (Cart) cartRepository.findByUserId(order.getUser().getId())
                            .orElseThrow(() -> new RuntimeException("Cart not found!"));
                    orderService.deleteCartAndItems(cart.getCartId()); // Xóa giỏ hàng
                    response.sendRedirect(redirectUrl + "?status=failed&orderId=" + orderId + "&message=payment_failed");
                    return;
                }
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(redirectUrl + "?status=error&message=invalid_order");
            return;
        } catch (RuntimeException e) {
            response.sendRedirect(redirectUrl + "?status=error&message=" + e.getMessage());
            return;
        }

        response.sendRedirect(redirectUrl + "?status=error&message=not_found");
    }

}
