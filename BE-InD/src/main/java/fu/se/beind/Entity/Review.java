package fu.se.beind.Entity;




import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "UserId")
    private User user;

    @ManyToOne
    @JoinColumn(name = "ProductId")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "OrderId")
    private Order order;

    private Byte rating;

    private String title;

    @Column(length = 1000)
    private String comment;

    @Column(columnDefinition = "nvarchar(max)")
    private String imageUrls;

    private Boolean isApproved;

    private LocalDateTime createdAt;
}