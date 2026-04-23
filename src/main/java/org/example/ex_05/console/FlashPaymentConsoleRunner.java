package org.example.ex_05.console;

import org.example.ex_05.dto.CheckoutLineRequest;
import org.example.ex_05.dto.CheckoutResult;
import org.example.ex_05.dto.RevenueRow;
import org.example.ex_05.dto.RevenueSummary;
import org.example.ex_05.model.Product;
import org.example.ex_05.model.Wallet;
import org.example.ex_05.service.BusinessException;
import org.example.ex_05.service.CheckoutService;
import org.example.ex_05.service.DataInitializer;
import org.example.ex_05.service.ProductService;
import org.example.ex_05.service.RevenueService;
import org.example.ex_05.service.WalletService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Component
@ConditionalOnProperty(name = "app.console.enabled", havingValue = "true", matchIfMissing = true)
public class FlashPaymentConsoleRunner implements CommandLineRunner {

    private final DataInitializer dataInitializer;
    private final WalletService walletService;
    private final ProductService productService;
    private final CheckoutService checkoutService;
    private final RevenueService revenueService;

    public FlashPaymentConsoleRunner(DataInitializer dataInitializer,
                                     WalletService walletService,
                                     ProductService productService,
                                     CheckoutService checkoutService,
                                     RevenueService revenueService) {
        this.dataInitializer = dataInitializer;
        this.walletService = walletService;
        this.productService = productService;
        this.checkoutService = checkoutService;
        this.revenueService = revenueService;
    }

    @Override
    public void run(String... args) {
        dataInitializer.seedIfEmpty();

        try (Scanner scanner = new Scanner(System.in)) {
            boolean running = true;
            while (running) {
                printMenu();
                int option = readInt(scanner, "Nhap lua chon: ");
                switch (option) {
                    case 1 -> topUpWalletFlow(scanner);
                    case 2 -> listProductsFlow();
                    case 3 -> checkoutFlow(scanner);
                    case 4 -> revenueFlow();
                    case 0 -> {
                        running = false;
                        System.out.println("Tạm biệt!");
                    }
                    default -> System.out.println("Lựa chọn không hợp lệ.");
                }
            }
        }
    }

    private void printMenu() {
        System.out.println("\n========== Rikkei Flash-Payment ==========");
        System.out.println("1. Nạp tiền ví");
        System.out.println("2. Xem danh sách sản phẩm & kho");
        System.out.println("3. Thanh toán đơn hàng đa vendor");
        System.out.println("4. Thống kê doanh thu");
        System.out.println("0. Thoát");
    }

    private void topUpWalletFlow(Scanner scanner) {
        try {
            List<Wallet> wallets = walletService.findAllWallets();
            if (wallets.isEmpty()) {
                System.out.println("Không có ví nào trong hệ thống.");
                return;
            }

            System.out.println("\nDanh sách ví:");
            wallets.forEach(w -> System.out.printf("- WalletId=%d | Owner=%s | Balance=%s%n",
                    w.getId(), w.getOwnerName(), w.getBalance()));

            Long walletId = readLong(scanner, "Nhập mã ví cần nạp: ");
            BigDecimal amount = readBigDecimal(scanner, "Nhập số tiền nạp: ");
            Wallet wallet = walletService.topUp(walletId, amount);
            System.out.println("Nạp tiền thành công. Số dư mới: " + wallet.getBalance());
        } catch (BusinessException ex) {
            System.out.println("Thất bại: " + ex.getMessage());
        }
    }

    private void listProductsFlow() {
        List<Product> products = productService.listProducts();
        if (products.isEmpty()) {
            System.out.println("Không có sản phẩm nào.");
            return;
        }

        System.out.println("\nDanh sách sản phẩm:");
        products.forEach(p -> System.out.printf("- MãSP=%d | Tên=%s | Nhà cung cấp=%s | Giá=%s | Tồn kho=%d%n",
                p.getId(), p.getName(), p.getVendor().getName(), p.getPrice(), p.getStock()));
    }

    private void checkoutFlow(Scanner scanner) {
        try {
            List<Wallet> wallets = walletService.findAllWallets();
            if (wallets.isEmpty()) {
                System.out.println("Không có ví nào để thanh toán.");
                return;
            }

            wallets.forEach(w -> System.out.printf("- Mã ví=%d | Chủ ví=%s | Số dư=%s%n",
                    w.getId(), w.getOwnerName(), w.getBalance()));
            Long walletId = readLong(scanner, "Nhập mã ví dùng để thanh toán: ");

            listProductsFlow();
            int lineCount = readInt(scanner, "Nhập số dòng sản phẩm muốn mua: ");
            if (lineCount <= 0) {
                throw new BusinessException("Số dòng sản phẩm phải lớn hơn 0");
            }

            List<CheckoutLineRequest> lines = new ArrayList<>();
            for (int i = 1; i <= lineCount; i++) {
                System.out.println("Dòng thứ " + i + ":");
                Long productId = readLong(scanner, "  Mã sản phẩm: ");
                int quantity = readInt(scanner, "  Số lượng: ");
                lines.add(new CheckoutLineRequest(productId, quantity));
            }

            CheckoutResult result = checkoutService.checkoutMultiVendor(walletId, lines);
            System.out.printf("Thành công: Mã đơn hàng=%d | Tổng tiền=%s%n", result.getOrderId(), result.getTotalAmount());
        } catch (BusinessException ex) {
            System.out.println("Giao dịch thất bại -> ROLLBACK. Lý do: " + ex.getMessage());
        } catch (RuntimeException ex) {
            System.out.println("Hệ thống gặp lỗi không mong muốn -> ROLLBACK.");
        }
    }

    private void revenueFlow() {
        RevenueSummary summary = revenueService.revenueSummary();
        List<RevenueRow> rows = revenueService.revenueByVendor();

        System.out.println("\nThống kê doanh thu:");
        System.out.println("- Tổng doanh thu hệ thống: " + summary.getTotalRevenue());
        System.out.println("- Số đơn hàng thành công: " + summary.getSuccessfulOrders());
        rows.forEach(row -> System.out.printf("  Mã vendor=%d | %s | Doanh thu=%s%n",
                row.getVendorId(), row.getVendorName(), row.getRevenue()));
    }

    private int readInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();
            try {
                return Integer.parseInt(input.trim());
            } catch (NumberFormatException ex) {
                System.out.println("Giá trị không hợp lệ, vui lòng nhập số nguyên.");
            }
        }
    }

    private long readLong(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();
            try {
                return Long.parseLong(input.trim());
            } catch (NumberFormatException ex) {
                System.out.println("Giá trị không hợp lệ, vui lòng nhập số nguyên.");
            }
        }
    }

    private BigDecimal readBigDecimal(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();
            try {
                return new BigDecimal(input.trim());
            } catch (NumberFormatException ex) {
                System.out.println("Giá trị không hợp lệ, vui lòng nhập số.");
            }
        }
    }
}

