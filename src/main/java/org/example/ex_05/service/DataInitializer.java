package org.example.ex_05.service;

import org.example.ex_05.model.Product;
import org.example.ex_05.model.Vendor;
import org.example.ex_05.model.Wallet;
import org.example.ex_05.repository.ProductRepository;
import org.example.ex_05.repository.VendorRepository;
import org.example.ex_05.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class DataInitializer {

    private final WalletRepository walletRepository;
    private final VendorRepository vendorRepository;
    private final ProductRepository productRepository;

    public DataInitializer(WalletRepository walletRepository,
                           VendorRepository vendorRepository,
                           ProductRepository productRepository) {
        this.walletRepository = walletRepository;
        this.vendorRepository = vendorRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public void seedIfEmpty() {
        if (walletRepository.countAll() == 0) {
            Wallet wallet = new Wallet();
            wallet.setOwnerName("Khách hàng chính");
            wallet.setBalance(new BigDecimal("500000"));
            walletRepository.save(wallet);
        }

        if (vendorRepository.countAll() == 0) {
            Vendor a = new Vendor();
            a.setName("Nhà cung cấp A");
            vendorRepository.save(a);

            Vendor b = new Vendor();
            b.setName("Nhà cung cấp B");
            vendorRepository.save(b);

            Vendor c = new Vendor();
            c.setName("Nhà cung cấp C");
            vendorRepository.save(c);
        }

        if (productRepository.countAll() == 0) {
            List<Vendor> vendors = vendorRepository.findAll();
            if (vendors.size() < 3) {
                return;
            }

            Product p1 = new Product();
            p1.setName("Khóa học Java Core");
            p1.setPrice(new BigDecimal("120000"));
            p1.setStock(20);
            p1.setVendor(vendors.get(0));
            productRepository.save(p1);

            Product p2 = new Product();
            p2.setName("Khóa học Spring Boot");
            p2.setPrice(new BigDecimal("180000"));
            p2.setStock(15);
            p2.setVendor(vendors.get(0));
            productRepository.save(p2);

            Product p3 = new Product();
            p3.setName("Khóa học React Frontend");
            p3.setPrice(new BigDecimal("140000"));
            p3.setStock(25);
            p3.setVendor(vendors.get(1));
            productRepository.save(p3);

            Product p4 = new Product();
            p4.setName("Bộ công cụ DevOps");
            p4.setPrice(new BigDecimal("210000"));
            p4.setStock(12);
            p4.setVendor(vendors.get(2));
            productRepository.save(p4);
        }
    }
}

