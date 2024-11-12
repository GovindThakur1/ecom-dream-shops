package com.govind.dreamshops.service.product;

import com.govind.dreamshops.dto.ImageDto;
import com.govind.dreamshops.dto.ProductDto;
import com.govind.dreamshops.exceptions.AlreadyExistsException;
import com.govind.dreamshops.exceptions.ResourceNotFoundException;
import com.govind.dreamshops.model.Category;
import com.govind.dreamshops.model.Image;
import com.govind.dreamshops.model.Product;
import com.govind.dreamshops.repository.CategoryRepository;
import com.govind.dreamshops.repository.ImageRepository;
import com.govind.dreamshops.repository.ProductRepository;
import com.govind.dreamshops.request.AddProductRequest;
import com.govind.dreamshops.request.ProductUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    private final ImageRepository imageRepository;

    @Override
    public Product addProduct(AddProductRequest request) {
        // Check if the Category is found in the database.
        // If yes, set it as a new product's category.
        // If no, then save it as a new category first and then set it as the new product's category.
        if (productExists(request.getName(), request.getBrand())) {
            throw new AlreadyExistsException(request.getBrand() + " " + request.getName() + " already exists!");
        }

        Category category = Optional.ofNullable(categoryRepository.findByName(request.getCategory().getName()))
                .orElseGet(() -> {
                    Category newCategory = new Category(request.getCategory().getName());
                    return categoryRepository.save(newCategory);
                });
        request.setCategory(category);
        return productRepository.save(createProduct(request, category));
    }

    private boolean productExists(String name, String brand) {
        return productRepository.existsByNameAndBrand(name, brand);
    }


    private Product createProduct(AddProductRequest request, Category category) {
        return new Product(
                request.getName(),
                request.getBrand(),
                request.getPrice(),
                request.getInventory(),
                request.getDescription(),
                category
        );
    }


    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found!"));
    }

    @Override
    public void deleteProductById(Long id) {
        productRepository.findById(id)
                .ifPresentOrElse(
                        productRepository::delete,
                        () -> {
                            throw new ResourceNotFoundException("Product not found!");
                        }
                );
    }


    @Override
    public Product updateProduct(ProductUpdateRequest request, Long productId) {
        return productRepository.findById(productId)
                .map(existingProduct -> updateExistingProduct(existingProduct, request))
                .map(productRepository::save)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found!"));
    }

    private Product updateExistingProduct(Product existingProduct, ProductUpdateRequest request) {
        if (request.getName() != null)
            existingProduct.setName(request.getName());
        if (request.getBrand() != null)
            existingProduct.setBrand(request.getBrand());
        if (request.getPrice() != null)
            existingProduct.setPrice(request.getPrice());
        if (request.getInventory() != 0)
            existingProduct.setInventory(request.getInventory());
        if (request.getDescription() != null)
            existingProduct.setDescription(request.getDescription());
        if (request.getCategory().getName() != null) {
            Category category = categoryRepository.findByName(request.getCategory().getName());
            existingProduct.setCategory(category);
        }
        return existingProduct;
    }


    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public List<Product> getProductsByCategory(String categoryName) {
        return productRepository.findByCategoryName(categoryName);
    }

    @Override
    public List<Product> getProductsByBrand(String brand) {
        return productRepository.findByBrand(brand);
    }

    @Override
    public List<Product> getProductByCategoryAndBrand(String categoryName, String brand) {
        return productRepository.findByCategoryNameAndBrand(categoryName, brand);
    }

    @Override
    public List<Product> getProductsByName(String name) {
        return productRepository.findByName(name);
    }

    @Override
    public List<Product> getProductByBrandAndName(String brand, String name) {
        return productRepository.findByBrandAndName(brand, name);
    }

    @Override
    public Long countProductsByBrandAndName(String brand, String name) {
        return productRepository.countByBrandAndName(brand, name);
    }


    @Override
    public List<ProductDto> getConvertedProducts(List<Product> products) {
        return products.stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    public ProductDto convertToDto(Product product) {
        ProductDto productDto = modelMapper.map(product, ProductDto.class);

        List<Image> images = imageRepository.findByProductId(product.getId());
        List<ImageDto> imageDtos = images.stream()
                .map(image -> modelMapper.map(image, ImageDto.class))
                .toList();

        productDto.setImages(imageDtos);
        return productDto;
    }


}











