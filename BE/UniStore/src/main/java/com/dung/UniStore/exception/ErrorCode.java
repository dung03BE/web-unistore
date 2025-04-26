package com.dung.UniStore.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;


@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Uncategorized error", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1002, "User existed", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1003, "Username must be at least {min} characters", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1004, "Password must be at least {min} characters", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1005, "User not existed", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "You do not have permission", HttpStatus.FORBIDDEN),
    INVALID_DOB(1008, "Your age must be at least {min}", HttpStatus.BAD_REQUEST),
    PRODUCT_NOT_EXISTED(1009, "PRODUCT not existed", HttpStatus.NOT_FOUND),
    CATEGORY_NOT_EXISTED(1010, "CATEGORY not existed", HttpStatus.NOT_FOUND),
    INVENTORY_ITEM_NOT_EXISTS(1011, "INVENTORY_ITEM not existed", HttpStatus.NOT_FOUND),
    ORDER_NOT_EXISTS(1012, "ORDER not existed", HttpStatus.NOT_FOUND),
    OutofStock(1013, "Out of Stock", HttpStatus.BAD_REQUEST),
    PRODUCT_EXISTS_CART(1014,"Product exists in the cart",HttpStatus.BAD_REQUEST),
    EMAIL_EXISTED(1015, "Email existed", HttpStatus.BAD_REQUEST),
    COUPON_IS_USED(1016,"Bạn đã sử dụng mã giảm giá này rồi!",HttpStatus.INTERNAL_SERVER_ERROR),
    LIMITED_USED(1017,"Mã giảm giá đã hết lượt sử dụng!",HttpStatus.BAD_REQUEST),
    COUPON_IS_ERROR(1018,"Mã giảm giá hết hạn!",HttpStatus.INTERNAL_SERVER_ERROR),

            ;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}
