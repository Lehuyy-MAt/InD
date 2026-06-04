package fu.se.beind.Entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ProductImages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "ProductId")
    private Product product;

    private String imageUrl;

    private String imageType;

    private String color;

    private Integer sortOrder;

    private Boolean isDefault;
}