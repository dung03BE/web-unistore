package com.dung.UniStore.mapper;

import com.dung.UniStore.dto.request.OrderCreationRequest;
import com.dung.UniStore.dto.response.OrderResponse;
import com.dung.UniStore.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {OrderDetailMapper.class})
public interface OrderMapper {
    @Mapping(target = "user.id", source = "userId")
    Order toOrder(OrderCreationRequest request);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "orderDetailList", source = "order.orderDetailList")
        //vậy target là field của orderresponse, còn source là Order
    OrderResponse toOrderResponse(Order order);
}
