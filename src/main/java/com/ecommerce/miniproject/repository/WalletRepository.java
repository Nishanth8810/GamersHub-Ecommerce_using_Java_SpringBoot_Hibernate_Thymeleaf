package com.ecommerce.miniproject.repository;

import com.ecommerce.miniproject.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
public interface WalletRepository extends JpaRepository<Wallet,Long> {
    Wallet findWalletByUserId(int id);
}
