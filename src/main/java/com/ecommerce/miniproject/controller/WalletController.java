package com.ecommerce.miniproject.controller;

import com.ecommerce.miniproject.dto.WalletDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WalletController {

    @GetMapping("/user/wallet/add")
    public String addWallet(Model model){
        model.addAttribute("walletDTO",new WalletDTO());
        return "wallet";
    }


}
