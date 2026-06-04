package fu.se.beind.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Designs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Design {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "UserId")
    private User user;

    @ManyToOne
    @JoinColumn(name = "ProductId")
    private Product product;

    private String designName;

    private String previewUrl;

    @Column(columnDefinition = "nvarchar(max)")
    private String designData;

    private String size;

    private String color;

    private Boolean isPublic;

    private Integer likeCount;

    private Integer viewCount;

    private LocalDateTime createdAt;


    @OneToMany(mappedBy = "design")
    private List<DesignInteraction> interactions = new ArrayList<>();

    @OneToMany(mappedBy = "design")
    private List<CartItem> cartItems = new ArrayList<>();

    @OneToMany(mappedBy = "design")
    private List<OrderItem> orderItems = new ArrayList<>();
}