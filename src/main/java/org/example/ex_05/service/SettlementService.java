package org.example.ex_05.service;

import org.example.ex_05.model.SettlementStatus;
import org.example.ex_05.model.Vendor;
import org.example.ex_05.model.VendorSettlementLog;
import org.example.ex_05.repository.SettlementLogRepository;
import org.example.ex_05.repository.VendorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class SettlementService {

    private final VendorRepository vendorRepository;
    private final SettlementLogRepository settlementLogRepository;

    public SettlementService(VendorRepository vendorRepository, SettlementLogRepository settlementLogRepository) {
        this.vendorRepository = vendorRepository;
        this.settlementLogRepository = settlementLogRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void settleVendor(Long orderId, Long vendorId, BigDecimal amount) {
        Vendor vendor = vendorRepository.findById(vendorId);
        if (vendor == null) {
            throw new BusinessException("Không tìm thấy vendor có mã=" + vendorId);
        }

        vendor.setTotalRevenue(vendor.getTotalRevenue().add(amount));

        VendorSettlementLog log = new VendorSettlementLog();
        log.setOrderId(orderId);
        log.setVendorId(vendorId);
        log.setAmount(amount);
        log.setStatus(SettlementStatus.SUCCESS);
        log.setMessage("THANH_TOÁN_VENDOR_THÀNH_CÔNG");
        log.setCreatedAt(LocalDateTime.now());
        settlementLogRepository.save(log);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logSettlementFailure(Long orderId, Long vendorId, BigDecimal amount, String message) {
        VendorSettlementLog log = new VendorSettlementLog();
        log.setOrderId(orderId);
        log.setVendorId(vendorId);
        log.setAmount(amount);
        log.setStatus(SettlementStatus.FAILED);
        log.setMessage(message == null || message.isBlank() ? "LỖI_KHÔNG_XÁC_ĐỊNH" : message);
        log.setCreatedAt(LocalDateTime.now());
        settlementLogRepository.save(log);
    }
}

