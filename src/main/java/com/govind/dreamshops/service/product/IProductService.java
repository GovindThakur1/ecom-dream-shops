package com.govind.dreamshops.service.product;

import com.govind.dreamshops.dto.ProductDto;
import com.govind.dreamshops.model.Product;
import com.govind.dreamshops.request.AddProductRequest;
import com.govind.dreamshops.request.ProductUpdateRequest;

import java.util.List;

public interface IProductService {
    Product addProduct(AddProductRequest request);

    Product getProductById(Long id);

    void deleteProductById(Long id);

    Product updateProduct(ProductUpdateRequest request, Long productId);

    List<Product> getAllProducts();

    List<Product> getProductsByCategory(String categoryName);

    List<Product> getProductsByBrand(String brand);

    List<Product> getProductByCategoryAndBrand(String categoryName, String brand);

    List<Product> getProductsByName(String name);

    List<Product> getProductByBrandAndName(String brand, String name);

    Long countProductsByBrandAndName(String brand, String name);

    List<ProductDto> getConvertedProducts(List<Product> products);

    ProductDto convertToDto(Product product);
}
