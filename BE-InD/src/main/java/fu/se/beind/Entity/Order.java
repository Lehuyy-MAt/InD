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
@Table(name = "Orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "UserId")
    private User user;

    @ManyToOne
    @JoinColumn(name = "DiscountCodeId")
    private DiscountCode discountCode;

    private String orderNumber;

    private String receiverName;

    private String receiverPhone;

    private String shippingAddress;

    private String city;

    private BigDecimal subtotal;

    private BigDecimal shippingFee;

    private BigDecimal discountAmount;

    private BigDecimal totalAmount;

    private String status;

    private String paymentMethod;

    private String note;

    private LocalDateTime createdAt;

    private LocalDateTime confirmedAt;

    private LocalDateTime deliveredAt;


    @OneToMany(mappedBy = "order")
    private List<OrderItem> orderItems = new ArrayList<>();
}