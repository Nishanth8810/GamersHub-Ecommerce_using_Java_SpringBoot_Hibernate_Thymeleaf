package com.ecommerce.miniproject.controller;

import com.ecommerce.miniproject.dto.AddressDTO;
import com.ecommerce.miniproject.dto.UserDTO;
import com.ecommerce.miniproject.entity.Address;
import com.ecommerce.miniproject.entity.User;
import com.ecommerce.miniproject.service.AddressService;
import com.ecommerce.miniproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
public class UserController {

    @Autowired
    UserService userService;
    @Autowired
    AddressService addressService;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;



    @GetMapping("/user")
    public String getUserDetail(Model model,Principal principal){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        User user = userService.getUserByEmail(currentPrincipalName).get();
        model.addAttribute(user);

        return "userProfile";
    }


    @GetMapping("/user/address")
    public String getUserAddress(Model model, Principal principal){

        String loggedUser= principal.getName();

        userService.getUserByEmail(loggedUser);
        List<Address> addressList=addressService.getAddressOfUser(loggedUser);

        model.addAttribute("userAddress",addressList);


        return "userAddress";
    }
    @GetMapping("/user/address/delete/{id}")
    public String getDeleteUserAddress(@PathVariable int id){
        addressService.deleteAddressByID(id);
        return "redirect:/user/address";
    }

    @GetMapping("user/address/update/{id}")
    public String updateUserAddress(@PathVariable int id,Model model,Principal principal){

        String loggedUser=principal.getName();

        User user = userService.getUserByEmail(loggedUser).get();

        Address address = addressService.getAddressOfUser(id);


        AddressDTO addressDTO= new AddressDTO();

        addressDTO.setAddress(address.getAddress());
        addressDTO.setName(address.getName());
        addressDTO.setId(address.getId());
        addressDTO.setCity(address.getCity());
        addressDTO.setPhoneNo(address.getPhoneNo());
        addressDTO.setUser(user);
        addressDTO.setLandmark(address.getAddress());
        addressDTO.setPincode(address.getPincode());

        model.addAttribute("addressDTO",addressDTO);
        return "addressAdd";
    }

    @GetMapping("user/addressAdd")
    public String addAddress(Model model){
        model.addAttribute("addressDTO",new AddressDTO());
        return "addressAdd";
    }

    @PostMapping("user/addressAdd")
    public String postAddAddress(@ModelAttribute("addressDTO")AddressDTO addressDTO,
                                 Model model,
    Principal principal){
        String loggedUser=principal.getName();
        User user = userService.getUserByEmail(loggedUser).get();
        Address address = new Address();
        address.setName(addressDTO.getName());
        address.setAddress(addressDTO.getAddress());
        address.setId(addressDTO.getId());
        address.setCity(addressDTO.getCity());
        address.setPincode(addressDTO.getPincode());
        address.setUser(user);
        address.setLandmark(addressDTO.getLandmark());
        address.setPhoneNo(addressDTO.getPhoneNo());
        addressService.addAddress(address);

        return "redirect:/user/address";
    }
    @GetMapping("user/userDetails")
    public String getUserDetails(Model model,Principal principal){
        String loggedUser=principal.getName();
        User user=userService.getUserByEmail(loggedUser).get();
        model.addAttribute("userDTO",user);
        return "userPage";

    }
    @PostMapping("user/userDetails")
    public String postUpdateUserDetails(@RequestParam("firstName")String firstName,
                                        @RequestParam("lastName") String lastName
                                       ,UserDTO userDTO ,Model model,Principal principal){


    String loggesUser=principal.getName();
    User userObj =userService.getUserByEmail(loggesUser).get();


        User user= new User();

        userDTO.setId(user.getId());
        userDTO.setEmail(userObj.getEmail());
        userDTO.setLastName(lastName);
        userDTO.setEmail(firstName);

//        user.setFirstName(firstName);
//        user.setLastName(lastName);
//        user.setEmail(userDTO.getEmail());


        userService.saveUser(user);

        return "redirect:/user";


    }

    @GetMapping ("/user/changePassword")
        public String getChangePassword(){
        return "changePassword";
    }

    @PostMapping("user/changePassword")
    public String postChangePassword(Principal principal,@RequestParam("oldPass") String oldPass,
                                     @RequestParam("newPass") String newPass){

        String loggedUser=principal.getName();
        User user=userService.getUserByEmail(loggedUser).get();

       boolean f= bCryptPasswordEncoder.matches(oldPass,user.getPassword());
       if (f){
          user.setPassword(bCryptPasswordEncoder.encode(newPass));
          userService.saveUser(user);
       }

        return "userProfile";
    }



}
