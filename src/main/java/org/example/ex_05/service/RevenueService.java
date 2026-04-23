package org.example.ex_05.service;

import org.example.ex_05.dto.RevenueRow;
import org.example.ex_05.dto.RevenueSummary;
import org.example.ex_05.model.OrderStatus;
import org.example.ex_05.model.Vendor;
import org.example.ex_05.repository.OrderRepository;
import org.example.ex_05.repository.VendorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class RevenueService {

    private final VendorRepository vendorRepository;
    private final OrderRepository orderRepository;

    public RevenueService(VendorRepository vendorRepository, OrderRepository orderRepository) {
        this.vendorRepository = vendorRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional(readOnly = true)
    public List<RevenueRow> revenueByVendor() {
        return vendorRepository.findAll().stream()
                .map(v -> new RevenueRow(v.getId(), v.getName(), v.getTotalRevenue()))
                .toList();
    }

    @Transactional(readOnly = true)
    public RevenueSummary revenueSummary() {
        List<Vendor> vendors = vendorRepository.findAll();
        BigDecimal total = vendors.stream()
                .map(Vendor::getTotalRevenue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        long successOrders = orderRepository.countByStatus(OrderStatus.SUCCESS);
        return new RevenueSummary(total, successOrders);
    }
}

