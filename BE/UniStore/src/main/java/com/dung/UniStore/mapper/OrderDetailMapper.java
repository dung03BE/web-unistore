package com.dung.UniStore.mapper;

import com.dung.UniStore.dto.request.OrderCreationRequest;
import com.dung.UniStore.dto.request.OrderDetailCreationRequest;
import com.dung.UniStore.dto.response.OrderDetailsResponse;
import com.dung.UniStore.dto.response.OrderResponse;
import com.dung.UniStore.entity.Order;
import com.dung.UniStore.entity.OrderDetail;
import com.dung.UniStore.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderDetailMapper {
    @Mapping(target = "product.id", source = "productId")
    @Mapping(target = "order.id", source = "orderId ")

    OrderDetail toOrder(OrderDetailCreationRequest request);

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "orderId", source = "order.id")
    @Mapping(target = "productResponses", source = "product")
    OrderDetailsResponse toOrderDetailsResponse(OrderDetail orderDetail);
    @Mapping(target = "name", source = "product.name")

    OrderDetailsResponse.ProductResponse toProductResponse(Product product);

    default List<OrderDetailsResponse.ProductResponse> mapProductToList(Product product) {
        if (product == null) return List.of(); // Nếu không có product, trả về list rỗng
        return List.of(toProductResponse(product));
    }
}
