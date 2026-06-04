package fu.se.beind.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "CategoryId")
    private Category category;

    private String name;

    private String description;

    private String material;

    private BigDecimal basePrice;

    private BigDecimal originalPrice;

    private String availableSizes;

    private String availableColors;

    private Integer stockQuantity;

    private Boolean isActive;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
    // Thêm danh sách ảnh liên kết (Một sản phẩm có nhiều ảnh)
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductImage> images; // Nhớ kiểm tra xem class của bạn tên là ProductImage hay Img để đặt kiểu dữ liệu cho đúng!

    @OneToMany(mappedBy = "product")
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "product")
    private List<Design> designs = new ArrayList<>();

    @OneToMany(mappedBy = "product")
    private List<CartItem> cartItems = new ArrayList<>();

    @OneToMany(mappedBy = "product")
    private List<OrderItem> orderItems = new ArrayList<>();}