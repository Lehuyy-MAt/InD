package fu.se.beind.Dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrderItemResponse {
    private Integer id;
    private Integer productId;
    private String productName;
    private Integer designId;
    private String designName;
    private String size;
    private String color;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
    private String printFileUrl;
    private String note;
}
