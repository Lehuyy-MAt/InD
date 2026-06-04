package fu.se.beind.Service;


import fu.se.beind.Dto.response.CategoryResponse;
import fu.se.beind.Dto.response.HomeResponse;
import fu.se.beind.Dto.response.ProductHomeResponse;
import fu.se.beind.Entity.Category;
import fu.se.beind.Entity.Product;
import fu.se.beind.Repo.CategoryRepository;
import fu.se.beind.Repo.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HomeService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public HomeResponse getHomeData() {
        HomeResponse response = new HomeResponse();

        // Danh mục
        response.setCategories(categoryRepository.findByIsActiveTrueOrderBySortOrderAsc()
                .stream().map(this::toCategoryResponse).collect(Collectors.toList()));

        // Sản phẩm nổi bật
        response.setFeaturedProducts(productRepository.findTop8ByIsActiveTrueOrderByCreatedAtDesc()
                .stream().map(this::toProductHomeResponse).collect(Collectors.toList()));

        // Sản phẩm mới
        response.setNewProducts(productRepository.findTop8ByIsActiveTrueOrderByIdDesc()
                .stream().map(this::toProductHomeResponse).collect(Collectors.toList()));

        return response;
    }

    private CategoryResponse toCategoryResponse(Category category) {
        CategoryResponse dto = new CategoryResponse();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        dto.setSlug(category.getSlug());
        dto.setImageUrl(category.getImageUrl());
        return dto;
    }

    private ProductHomeResponse toProductHomeResponse(Product product) {
        ProductHomeResponse dto = new ProductHomeResponse();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setBasePrice(product.getBasePrice());
        dto.setOriginalPrice(product.getOriginalPrice());

        // Lấy ảnh mặc định (nếu có)
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            product.getImages().stream()
                    .filter(img -> Boolean.TRUE.equals(img.getIsDefault()))
                    .findFirst()
                    .ifPresentOrElse(
                            img -> dto.setImageUrl(img.getImageUrl()),
                            () -> dto.setImageUrl(product.getImages().get(0).getImageUrl()) // lấy ảnh đầu tiên nếu không có default
                    );
        }

        return dto;
    }
}