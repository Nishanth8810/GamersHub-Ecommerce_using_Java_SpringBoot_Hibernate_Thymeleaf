package com.ecommerce.miniproject.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WalletDTO {
    private Long id;
    private Long userId;
    private Double balance;
}
