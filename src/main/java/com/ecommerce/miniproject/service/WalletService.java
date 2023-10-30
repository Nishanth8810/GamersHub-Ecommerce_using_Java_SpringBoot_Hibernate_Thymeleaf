package com.ecommerce.miniproject.service;

import com.ecommerce.miniproject.entity.Wallet;
import com.ecommerce.miniproject.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WalletService {
    @Autowired
    WalletRepository walletRepository;

    public void saveWallet(Wallet wallet){
        walletRepository.save(wallet);
    }

    public Wallet getWalletOfUser(int id) {
       return walletRepository.findWalletByUserId(id);
    }
}
