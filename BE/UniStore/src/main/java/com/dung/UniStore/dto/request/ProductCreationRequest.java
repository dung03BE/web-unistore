package com.dung.UniStore.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreationRequest {
        @NotBlank(message = "ProDuctName is required!")
        @Size(min=3, max=200,message = "ProductName must be between 3 and 200 characters")
        private String name;
        @Min(value =0 , message = "Price must be greater than or equal 0")
        @Max(value = 1000000000, message = "Price must be less than or equal 1000000000")
        private float price;
        private String description;
        private int categoryId;
        private Double discount;
        private String brand;
        private String model;
        private Byte available;
//        private List<String> imageUrls;
        private List<String> colors;
        private ProductDetailsRequest details;

}
