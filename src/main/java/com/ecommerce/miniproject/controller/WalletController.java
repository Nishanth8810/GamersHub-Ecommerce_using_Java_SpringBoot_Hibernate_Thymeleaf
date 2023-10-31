package com.ecommerce.miniproject.controller;

import com.ecommerce.miniproject.dto.WalletDTO;
import com.ecommerce.miniproject.entity.TransactionDetails;
import com.ecommerce.miniproject.entity.User;
import com.ecommerce.miniproject.entity.Wallet;
import com.ecommerce.miniproject.enums.OrderManagementMessages;
import com.ecommerce.miniproject.service.OrderService;
import com.ecommerce.miniproject.service.UserService;
import com.ecommerce.miniproject.service.WalletService;
import com.razorpay.RazorpayException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class WalletController {

    @Autowired
    UserService userService;
    @Autowired
    WalletService walletService;
    @Autowired
    OrderService orderService;

    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    @GetMapping("/user/wallet")
    public String getWallet(Model model){
        User user=userService.getUserByEmail(getCurrentUsername()).orElseThrow();
        Wallet wallet=walletService.getWalletOfUser(user.getId());
        model.addAttribute("walletDTO",new WalletDTO());
        model.addAttribute("balances",wallet.getBalance());
        return "wallet";
    }
    @PostMapping("/user/wallet")
    public String postAddWallet(@ModelAttribute("walletDTO") WalletDTO walletDTO,
                                HttpServletRequest httpServletRequest,
                                RedirectAttributes redirectAttributes,
                                Model model){
        Double total=walletDTO.getBalance();

        handleRazorpayPayment(total,httpServletRequest,redirectAttributes,model);

        return "walletRazorPayment";
    }

    public void handleRazorpayPayment(Double total, HttpServletRequest httpServletRequest,
                                      RedirectAttributes redirectAttributes,Model model) {

        try {
            TransactionDetails transactionDetails = orderService.createTransaction(total);
            String orderId = transactionDetails.getOrderId();
            HttpSession session = httpServletRequest.getSession();
            session.setAttribute("orderId", orderId);
            session.setAttribute("amount", total * 100);
            model.addAttribute("orderId", orderId);
            model.addAttribute("amount", total * 100);
        } catch (RazorpayException e) {
            redirectAttributes.addFlashAttribute("errorWallet",
                    OrderManagementMessages.WALLET_ERROR.getMessage());
        }
    }
    @GetMapping("/walletOrderSuccessful")
    public String walletSuccessTransaction(RedirectAttributes redirectAttributes, HttpSession httpSession){
        double total = Double.parseDouble(httpSession.getAttribute("amount").toString());
        User user=userService.getUserByEmail(getCurrentUsername()).orElseThrow();
        Wallet wallet=walletService.getWalletOfUser(user.getId());
        if (wallet!= null){
            double newAmount=wallet.getBalance()+total/100;
            wallet.setBalance(newAmount);
            walletService.saveWallet(wallet);
        }
        return "redirect:/user/wallet";
    }


}
