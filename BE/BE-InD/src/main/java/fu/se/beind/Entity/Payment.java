package fu.se.beind.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "Payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "OrderId")
    private Order order;

    private String transactionId;

    private BigDecimal amount;

    private String paymentMethod;

    private String status;

    @Column(columnDefinition = "nvarchar(max)")
    private String responseData;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}