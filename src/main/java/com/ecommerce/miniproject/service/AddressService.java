package com.ecommerce.miniproject.service;

import com.ecommerce.miniproject.entity.Address;
import com.ecommerce.miniproject.entity.User;
import com.ecommerce.miniproject.repository.AddressRepository;
import com.ecommerce.miniproject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AddressService {
    @Autowired
    AddressRepository addressRepository;

    @Autowired
    UserRepository userRepository;

    public void addAddress(Address address){
        addressRepository.save(address);
    }

    public List<Address> getAddressOfUser(String email){
        User user = userRepository.findUserByEmail(email).get();
        return addressRepository.findByUser(user);
    }

    public void deleteAddressByID(int id) {
        addressRepository.deleteById(id);
    }

    public Address getAddressOfUser(int id) {
      return addressRepository.findById(id).get();

    }

    public Address getAddressById(int id) {
        return  addressRepository.findById(id).get();
    }


    public void setDefaultAddressForUser(User user, Address defaultAddress) {
        List<Address> userAddresses = addressRepository.findByUser(user);
        for (Address address : userAddresses) {
            address.setDefault(address.equals(defaultAddress));
        }
        addressRepository.saveAll(userAddresses);
    }
}

