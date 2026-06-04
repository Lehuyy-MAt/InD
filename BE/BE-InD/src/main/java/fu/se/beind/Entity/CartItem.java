package fu.se.beind.Entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "CartItems")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "CartId")
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "ProductId")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "DesignId")
    private Design design;

    private String size;

    private String color;

    private Integer quantity;

    private BigDecimal unitPrice;

    private LocalDateTime addedAt;
}