package fu.se.beind.Controller;


import fu.se.beind.Dto.request.ProductImageRequest;
import fu.se.beind.Dto.request.ProductRequest;
import fu.se.beind.Dto.response.PageResponse;
import fu.se.beind.Dto.response.ProductDetailResponse;
import fu.se.beind.Dto.response.ProductImageResponse;
import fu.se.beind.Dto.response.ProductListResponse;
import fu.se.beind.Service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // ──────────────────────────────────────────────
    //  PUBLIC
    // ──────────────────────────────────────────────

    /**
     * GET /api/products/{id}
     * Xem chi tiết sản phẩm (kèm ảnh, rating, số lượng review)
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductDetailResponse> getProductDetail(@PathVariable Integer id) {
        return ResponseEntity.ok(productService.getProductDetail(id));
    }

    /**
     * GET /api/products/search
     * Tìm kiếm / lọc sản phẩm có phân trang
     *
     * Query params:
     *   categoryId, keyword, minPrice, maxPrice,
     *   page (default 0), size (default 12),
     *   sortBy (default "createdAt"), sortDir (default "desc")
     */
    @GetMapping("/search")
    public ResponseEntity<PageResponse<ProductListResponse>> searchProducts(
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0")          int page,
            @RequestParam(defaultValue = "12")         int size,
            @RequestParam(defaultValue = "createdAt")  String sortBy,
            @RequestParam(defaultValue = "desc")       String sortDir
    ) {
        return ResponseEntity.ok(
                productService.searchProducts(categoryId, keyword, minPrice, maxPrice, page, size, sortBy, sortDir)
        );
    }

    /**
     * GET /api/products/category/{categoryId}
     * Lấy tất cả sản phẩm theo danh mục
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductListResponse>> getByCategory(@PathVariable Integer categoryId) {
        return ResponseEntity.ok(productService.getByCategory(categoryId));
    }

    /**
     * GET /api/products/{id}/images
     * Lấy danh sách ảnh của sản phẩm
     */
    @GetMapping("/{id}/images")
    public ResponseEntity<List<ProductImageResponse>> getImages(@PathVariable Integer id) {
        return ResponseEntity.ok(productService.getImages(id));
    }

    // ──────────────────────────────────────────────
    //  ADMIN
    // ──────────────────────────────────────────────

    /**
     * GET /api/products/admin
     * Admin: danh sách tất cả sản phẩm (kể cả inactive) có phân trang + tìm kiếm
     */
    @GetMapping("/admin")
    public ResponseEntity<PageResponse<ProductListResponse>> getAllForAdmin(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(productService.getAllForAdmin(keyword, isActive, page, size));
    }

    /**
     * POST /api/products
     * Admin: tạo sản phẩm mới
     */
    @PostMapping
    public ResponseEntity<ProductDetailResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(request));
    }

    /**
     * PUT /api/products/{id}
     * Admin: cập nhật thông tin sản phẩm
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductDetailResponse> updateProduct(
            @PathVariable Integer id,
            @Valid @RequestBody ProductRequest request
    ) {
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    /**
     * PATCH /api/products/{id}/toggle-active
     * Admin: ẩn / hiện sản phẩm
     */
    @PatchMapping("/{id}/toggle-active")
    public ResponseEntity<ProductDetailResponse> toggleActive(@PathVariable Integer id) {
        return ResponseEntity.ok(productService.toggleActive(id));
    }

    /**
     * DELETE /api/products/{id}
     * Admin: xoá vĩnh viễn sản phẩm
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Integer id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    // ──────────────────────────────────────────────
    //  ADMIN: quản lý ảnh
    // ──────────────────────────────────────────────

    /**
     * POST /api/products/{id}/images
     * Admin: thêm ảnh vào sản phẩm
     */
    @PostMapping("/{id}/images")
    public ResponseEntity<ProductImageResponse> addImage(
            @PathVariable Integer id,
            @Valid @RequestBody ProductImageRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.addImage(id, request));
    }

    /**
     * PUT /api/products/{id}/images/{imageId}
     * Admin: cập nhật ảnh
     */
    @PutMapping("/{id}/images/{imageId}")
    public ResponseEntity<ProductImageResponse> updateImage(
            @PathVariable Integer id,
            @PathVariable Integer imageId,
            @Valid @RequestBody ProductImageRequest request
    ) {
        return ResponseEntity.ok(productService.updateImage(id, imageId, request));
    }

    /**
     * DELETE /api/products/{id}/images/{imageId}
     * Admin: xoá ảnh
     */
    @DeleteMapping("/{id}/images/{imageId}")
    public ResponseEntity<Void> deleteImage(
            @PathVariable Integer id,
            @PathVariable Integer imageId
    ) {
        productService.deleteImage(id, imageId);
        return ResponseEntity.noContent().build();
    }
}
