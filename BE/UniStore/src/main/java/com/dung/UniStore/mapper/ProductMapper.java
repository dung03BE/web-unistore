package com.dung.UniStore.mapper;

import com.dung.UniStore.dto.request.ProductCreationRequest;
import com.dung.UniStore.dto.request.ProductUpdateRequest;
import com.dung.UniStore.dto.request.UserCreationRequest;
import com.dung.UniStore.dto.request.UserUpdateRequest;
import com.dung.UniStore.dto.response.ProductColorResponse;
import com.dung.UniStore.dto.response.ProductDetailsResponse;
import com.dung.UniStore.dto.response.ProductResponse;
import com.dung.UniStore.dto.response.UserResponse;
import com.dung.UniStore.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {

    @Mapping(target = "category.id", source = "categoryId")
    Product toProduct(ProductCreationRequest request);

    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "thumbnails", source = "images") // Đổi tên 'images' thành 'thumbnails' trong DTO
    @Mapping(target = "colors",source = "colors")
    ProductResponse toProductResponse(Product product);
    void updateProduct(@MappingTarget Product product, ProductUpdateRequest request);

    // Phương thức ánh xạ từng ProductImage sang ProductImageResponse
    ProductResponse.ProductImageResponse toProductImageResponse(ProductImage image);

    // Phương thức ánh xạ danh sách ProductImage
    List<ProductResponse.ProductImageResponse> toProductImageResponses(List<ProductImage> images);
    // Ánh xạ ProductDetails sang ProductDetailsResponse
    ProductDetailsResponse toProductDetailsResponse(ProductDetails details);
    // Thêm phương thức ánh xạ ProductColor sang ProductColorResponse
    ProductResponse.ProductColorResponse toProductColorResponse(ProductColor productColor);

    List<ProductResponse.ProductColorResponse> toProductColorResponses(List<ProductColor> productColors);

}
