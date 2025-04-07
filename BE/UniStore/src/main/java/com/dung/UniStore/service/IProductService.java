package com.dung.UniStore.service;

import com.dung.UniStore.dto.request.ProductCreationRequest;
import com.dung.UniStore.dto.request.ProductUpdateRequest;
import com.dung.UniStore.dto.response.ProductImageResponse;
import com.dung.UniStore.dto.response.ProductResponse;
import com.dung.UniStore.entity.Product;
import com.dung.UniStore.entity.ProductImage;
import com.dung.UniStore.form.ProductFilterForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IProductService {
    Page<ProductResponse> getAllProducts(Pageable pageable, ProductFilterForm form);

    ProductResponse getProductById(int id);

    ProductResponse updateProduct(int id, ProductUpdateRequest request);

    void deleteProduct(int id);

    ProductResponse getProductByName(String name);

    ProductResponse createProduct(ProductCreationRequest request);

    ProductImage createProductImage(int id, ProductImageResponse build) throws Exception;

    List<ProductResponse> getAll();

    Page<ProductResponse> getAllProductsByCategory(Pageable pageable,int categoryId);

    List<ProductResponse> getProductsByIds(List<Integer> ids);
}
