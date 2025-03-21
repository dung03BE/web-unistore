package com.dung.UniStore.service;

import com.dung.UniStore.dto.request.OrderConfirmationMessage;
import com.dung.UniStore.dto.response.OrderDetailsResponse;
import com.dung.UniStore.dto.response.OrderResponse;
import com.dung.UniStore.entity.OrderDetail;
import com.google.zxing.WriterException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender javaMailSender;
    @Async
    public void sendOrderConfirmationEmail(OrderConfirmationMessage message) {
        try {
            String esimData = generateEsimData(message);
            byte[] qrCodeImage = QRCodeGenerator.generateQRCodeImage(esimData);
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setTo(message.getCustomerEmail());
            helper.setSubject("Nguyen Chi Dung come here");
            helper.setText("Tôi là Nguyen Chi Dung hay quyet QR ben duoi nhe !!!" + message.getId(), true);
            // Đính kèm mã QR vào email
            helper.addAttachment("esim-qr-code.png", new ByteArrayResource(qrCodeImage));
            javaMailSender.send(mimeMessage);
        } catch (MessagingException | IOException | WriterException e) {
            e.printStackTrace();
        }
    }
    private String generateEsimData(OrderConfirmationMessage message) {
        // Tạo dữ liệu eSIM từ thông điệp đơn hàng (ví dụ: ID eSIM, thông tin cấu hình)
        return "Chào em Loan anh là DUNGX đẹp trai hahahaha, hay gui cho ANH 2 link fb em nhe, " + message.getId();
    }
    public String buildOrderDetailsEmail(OrderResponse orderResponse) {

        StringBuilder emailContent = new StringBuilder();
        emailContent.append("Dear " + orderResponse.getFullName() + ",\n\n");
        emailContent.append("Thank you for your order! Here are the details of your order:\n\n");
        emailContent.append("Order ID: " + orderResponse.getUserId() + "\n");
        emailContent.append("Order Date: " + orderResponse.getOrderDate() + "\n");
        emailContent.append("Total Amount: " + orderResponse.getTotalMoney() + " VND\n\n");
        emailContent.append("Order Details:\n");

        for (OrderDetailsResponse detail : orderResponse.getOrderDetailList()) {
            emailContent.append("Product ID: " + detail.getProductId() + ", ");
            emailContent.append("Quantity: " + detail.getQuantity() + ", ");
            emailContent.append("Price: " + detail.getPrice() + "\n");
        }

        emailContent.append("\nShipping Method: " + orderResponse.getShippingMethod() + "\n");
        emailContent.append("Shipping Address: " + orderResponse.getShippingAddress() + "\n");
        emailContent.append("\nThank you for shopping with us!");

        return emailContent.toString();
    }
    public void sendCouponNotification(String to, String couponCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("🎉 Bạn nhận được phiếu giảm giá!");
        message.setText("Xin chào, bạn đã nhận được phiếu giảm giá 50%! Mã của bạn: " + couponCode);
        message.setText("Hãy truy cập website http://localhost:3001/ để nhận sử dụng ưu đãi này!");
        javaMailSender.send(message);
    }
}
