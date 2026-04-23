package org.example.ex_05.service;

import org.example.ex_05.model.Wallet;
import org.example.ex_05.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class WalletService {

    private final WalletRepository walletRepository;

    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    @Transactional(readOnly = true)
    public List<Wallet> findAllWallets() {
        return walletRepository.findAll();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Wallet topUp(Long walletId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Số tiền nạp phải lớn hơn 0");
        }

        Wallet wallet = walletRepository.findByIdForUpdate(walletId);
        if (wallet == null) {
            throw new BusinessException("Không tìm thấy ví với mã=" + walletId);
        }

        wallet.setBalance(wallet.getBalance().add(amount));
        return wallet;
    }
}

