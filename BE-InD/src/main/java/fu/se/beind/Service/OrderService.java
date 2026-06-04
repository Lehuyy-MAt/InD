package fu.se.beind.Service;

import fu.se.beind.Dto.request.OrderRequest;
import fu.se.beind.Dto.response.OrderItemResponse;
import fu.se.beind.Dto.response.OrderResponse;
import fu.se.beind.Entity.*;
import fu.se.beind.Repo.*;
import fu.se.beind.exception.BadRequestException;
import fu.se.beind.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final DesignRepository designRepository;
    private final DiscountCodeRepository discountCodeRepository;

    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng không tồn tại"));

        Order order = new Order();
        order.setUser(user);
        order.setOrderNumber("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        order.setReceiverName(request.getReceiverName());
        order.setReceiverPhone(request.getReceiverPhone());
        order.setShippingAddress(request.getShippingAddress());
        order.setCity(request.getCity());
        order.setPaymentMethod(request.getPaymentMethod());
        order.setNote(request.getNote());
        order.setShippingFee(BigDecimal.valueOf(30000)); // phí giao hàng cố định

        // Build order items
        List<OrderItem> items = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;

        for (OrderRequest.OrderItemRequest itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Sản phẩm không tồn tại: " + itemReq.getProductId()));

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setProductName(product.getName());
            item.setSize(itemReq.getSize());
            item.setColor(itemReq.getColor());
            item.setQuantity(itemReq.getQuantity());
            item.setUnitPrice(itemReq.getUnitPrice());
            item.setSubtotal(itemReq.getUnitPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity())));
            item.setNote(itemReq.getNote());

            if (itemReq.getDesignId() != null) {
                Design design = designRepository.findById(itemReq.getDesignId())
                        .orElseThrow(() -> new ResourceNotFoundException("Thiết kế không tồn tại"));
                item.setDesign(design);
            }

            items.add(item);
            subtotal = subtotal.add(item.getSubtotal());
        }

        // ĐÃ SỬA: Đổi từ setItems() thành setOrderItems() để khớp với Entity Order.java của bạn
        order.setOrderItems(items);
        order.setSubtotal(subtotal);

        // Áp dụng mã giảm giá
        BigDecimal discountAmount = BigDecimal.ZERO;
        if (request.getDiscountCode() != null && !request.getDiscountCode().isBlank()) {
            DiscountCode discount = discountCodeRepository.findByCode(request.getDiscountCode())
                    .orElseThrow(() -> new BadRequestException("Mã giảm giá không hợp lệ"));

            if (!Boolean.TRUE.equals(discount.getIsActive())) {
                throw new BadRequestException("Mã giảm giá không còn hoạt động");
            }

            if (subtotal.compareTo(discount.getMinOrderAmount()) < 0) {
                throw new BadRequestException("Đơn hàng chưa đạt giá trị tối thiểu để áp dụng mã");
            }

            if ("Percentage".equals(discount.getDiscountType())) {
                // ĐÃ SỬA: Thêm RoundingMode.HALF_UP để tránh lỗi sập app khi phép chia bị số thập phân vô hạn
                discountAmount = subtotal.multiply(discount.getDiscountValue())
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            } else {
                discountAmount = discount.getDiscountValue();
            }

            if (discount.getMaxDiscount() != null && discountAmount.compareTo(discount.getMaxDiscount()) > 0) {
                discountAmount = discount.getMaxDiscount();
            }

            // ĐÃ SỬA: Đổi từ setDiscountCodeId(id) thành setDiscountCode(object) theo cấu hình @ManyToOne của bạn
            order.setDiscountCode(discount);
            discount.setUsedCount(discount.getUsedCount() + 1);
            discountCodeRepository.save(discount);
        }

        order.setDiscountAmount(discountAmount);
        order.setTotalAmount(subtotal.add(order.getShippingFee()).subtract(discountAmount));

        Order saved = orderRepository.save(order);
        return toOrderResponse(saved);
    }

    public List<OrderResponse> getOrdersByUserId(Integer userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toOrderResponse)
                .collect(Collectors.toList());
    }

    public OrderResponse getOrderById(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Đơn hàng không tồn tại với id: " + orderId));
        return toOrderResponse(order);
    }

    @Transactional
    public OrderResponse cancelOrder(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Đơn hàng không tồn tại với id: " + orderId));

        if (!"Pending".equals(order.getStatus())) {
            throw new BadRequestException("Chỉ có thể huỷ đơn hàng ở trạng thái Pending");
        }

        order.setStatus("Cancelled");
        return toOrderResponse(orderRepository.save(order));
    }

    @Transactional
    public OrderResponse updateOrderStatus(Integer orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Đơn hàng không tồn tại với id: " + orderId));

        order.setStatus(status);

        if ("Confirmed".equals(status)) {
            order.setConfirmedAt(LocalDateTime.now());
        } else if ("Delivered".equals(status)) {
            order.setDeliveredAt(LocalDateTime.now());
        }

        return toOrderResponse(orderRepository.save(order));
    }

    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toOrderResponse)
                .collect(Collectors.toList());
    }

    // ────────────────────────── helpers ──────────────────────────

    private OrderResponse toOrderResponse(Order order) {
        OrderResponse dto = new OrderResponse();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setUserId(order.getUser().getId());
        dto.setUserFullName(order.getUser().getFullName());
        dto.setReceiverName(order.getReceiverName());
        dto.setReceiverPhone(order.getReceiverPhone());
        dto.setShippingAddress(order.getShippingAddress());
        dto.setCity(order.getCity());
        dto.setSubtotal(order.getSubtotal());
        dto.setShippingFee(order.getShippingFee());
        dto.setDiscountAmount(order.getDiscountAmount());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setNote(order.getNote());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setConfirmedAt(order.getConfirmedAt());
        dto.setDeliveredAt(order.getDeliveredAt());

        // ĐÃ SỬA: Đổi từ order.getItems() thành order.getOrderItems() để khớp với Entity Order.java của bạn
        List<OrderItemResponse> itemResponses = order.getOrderItems().stream()
                .map(this::toOrderItemResponse)
                .collect(Collectors.toList());
        dto.setItems(itemResponses);

        return dto;
    }

    private OrderItemResponse toOrderItemResponse(OrderItem item) {
        OrderItemResponse dto = new OrderItemResponse();
        dto.setId(item.getId());
        dto.setProductId(item.getProduct().getId());
        dto.setProductName(item.getProductName());
        dto.setSize(item.getSize());
        dto.setColor(item.getColor());
        dto.setQuantity(item.getQuantity());
        dto.setUnitPrice(item.getUnitPrice());
        dto.setSubtotal(item.getSubtotal());
        dto.setPrintFileUrl(item.getPrintFileUrl());
        dto.setNote(item.getNote());

        if (item.getDesign() != null) {
            dto.setDesignId(item.getDesign().getId());
            dto.setDesignName(item.getDesign().getDesignName());
        }

        return dto;
    }
}