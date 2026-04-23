package org.example.ex_05.service;

import org.example.ex_05.dto.CheckoutLineRequest;
import org.example.ex_05.dto.CheckoutResult;
import org.example.ex_05.model.CustomerOrder;
import org.example.ex_05.model.OrderItem;
import org.example.ex_05.model.OrderStatus;
import org.example.ex_05.model.Product;
import org.example.ex_05.model.Wallet;
import org.example.ex_05.repository.OrderRepository;
import org.example.ex_05.repository.ProductRepository;
import org.example.ex_05.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class CheckoutService {

    private final WalletRepository walletRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final SettlementService settlementService;

    public CheckoutService(WalletRepository walletRepository,
                           ProductRepository productRepository,
                           OrderRepository orderRepository,
                           SettlementService settlementService) {
        this.walletRepository = walletRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.settlementService = settlementService;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public CheckoutResult checkoutMultiVendor(Long walletId, List<CheckoutLineRequest> lines) {
        if (lines == null || lines.isEmpty()) {
            throw new BusinessException("Đơn hàng rỗng");
        }

        Map<Long, Integer> mergedLines = mergeLines(lines);
        Wallet wallet = walletRepository.findByIdForUpdate(walletId);
        if (wallet == null) {
            throw new BusinessException("Không tìm thấy ví có mã=" + walletId);
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();
        Map<Long, BigDecimal> amountByVendor = new LinkedHashMap<>();

        for (Map.Entry<Long, Integer> entry : mergedLines.entrySet()) {
            Long productId = entry.getKey();
            int quantity = entry.getValue();

            Product product = productRepository.findByIdForUpdate(productId);
            if (product == null) {
                throw new BusinessException("Sản phẩm có mã=" + productId + " không tồn tại hoặc đã bị xóa");
            }
            if (quantity <= 0) {
                throw new BusinessException("Số lượng mua phải lớn hơn 0 cho sản phẩm có mã=" + productId);
            }
            if (product.getStock() < quantity) {
                throw new BusinessException("Sản phẩm " + product.getName() + " không đủ tồn kho");
            }

            BigDecimal lineAmount = product.getPrice().multiply(BigDecimal.valueOf(quantity));
            totalAmount = totalAmount.add(lineAmount);

            OrderItem item = new OrderItem();
            item.setProduct(product);
            item.setQuantity(quantity);
            item.setPriceAtPurchase(product.getPrice());
            item.setLineAmount(lineAmount);
            orderItems.add(item);

            Long vendorId = product.getVendor().getId();
            amountByVendor.put(vendorId, amountByVendor.getOrDefault(vendorId, BigDecimal.ZERO).add(lineAmount));
        }

        if (wallet.getBalance().compareTo(totalAmount) < 0) {
            throw new BusinessException("Số dư ví không đủ. Số dư hiện tại: " + wallet.getBalance());
        }

        wallet.setBalance(wallet.getBalance().subtract(totalAmount));

        CustomerOrder order = new CustomerOrder();
        order.setWallet(wallet);
        order.setTotalAmount(totalAmount);
        order.setStatus(OrderStatus.SUCCESS);
        order.setCreatedAt(LocalDateTime.now());
        orderRepository.saveOrder(order);

        for (OrderItem item : orderItems) {
            Product product = item.getProduct();
            product.setStock(product.getStock() - item.getQuantity());
            item.setOrder(order);
            orderRepository.saveOrderItem(item);
        }

        for (Map.Entry<Long, BigDecimal> vendorEntry : amountByVendor.entrySet()) {
            Long vendorId = vendorEntry.getKey();
            BigDecimal vendorAmount = vendorEntry.getValue();
            try {
                settlementService.settleVendor(order.getId(), vendorId, vendorAmount);
            } catch (RuntimeException ex) {
                settlementService.logSettlementFailure(order.getId(), vendorId, vendorAmount, ex.getMessage());
            }
        }

        return new CheckoutResult(order.getId(), totalAmount, "Thanh toan thanh cong");
    }

    private Map<Long, Integer> mergeLines(List<CheckoutLineRequest> lines) {
        Map<Long, Integer> merged = new LinkedHashMap<>();
        for (CheckoutLineRequest line : lines) {
            if (line.getProductId() == null) {
                throw new BusinessException("Mã sản phẩm không được để trống");
            }
            merged.put(line.getProductId(), merged.getOrDefault(line.getProductId(), 0) + line.getQuantity());
        }
        return merged;
    }
}

