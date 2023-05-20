package com.bilskik.onlineshop.services;

import com.bilskik.onlineshop.auth.JwtService;
import com.bilskik.onlineshop.controllers.CustomerController;
import com.bilskik.onlineshop.entities.Cart;
import com.bilskik.onlineshop.exception.DuplicateEntryException;
import com.bilskik.onlineshop.http.AuthenticationRequest;
import com.bilskik.onlineshop.http.AuthenticationResponse;
import com.bilskik.onlineshop.http.RegisterRequest;
import com.bilskik.onlineshop.dto.CustomerDTO;
import com.bilskik.onlineshop.entities.Customer;
import com.bilskik.onlineshop.enumeration.Role;
import com.bilskik.onlineshop.mapper.MapperImpl;
import com.bilskik.onlineshop.repositories.CartRepository;
import com.bilskik.onlineshop.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerService {
    Logger logger = LoggerFactory.getLogger(CustomerService.class);

    private final CustomerRepository customerRepository;
    private final CartRepository cartRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
//    private final Mapper modelMapper;
    @Autowired
    private MapperImpl<Customer,CustomerDTO> customerMapper;
//    private final Mapper<Customer,CustomerDTO> map;
    public AuthenticationResponse register(RegisterRequest request) {
        Cart cart = Cart.builder()
                .productList(new ArrayList<>())
                .build();
//        cartRepository.save(cart);
        Customer customer = Customer.builder()
                .name(request.getName())
                .surename(request.getSurename())
                .email(request.getEmail())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.CUSTOMER)
                .cart(cart)
                .build();
        try {
            logger.info(customer.getEmail());

            customerRepository.save(customer);
        }
        catch(DataIntegrityViolationException  e) {
            logger.info(e.toString());
            throw new DuplicateEntryException("Email already exist!");
        }
        String jwtToken = jwtService.generateToken(customer);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        Optional<Customer> user = customerRepository.findByEmail(request.getEmail());
        if(user.isEmpty()) {
            throw new NoSuchElementException("Email doesn't exist!");
        }
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        }
        catch(BadCredentialsException e) {
            throw new BadCredentialsException("Invalid Password!");
        }
        String jwtToken = jwtService.generateToken(user.get());
        logger.info(jwtToken);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public List<CustomerDTO> getCustomersList() {
        List<Customer> customerList = customerRepository.findAll();
        return customerList.stream()
                .map(customerMapper::toDTO)
                .collect(Collectors.toList());
    }

    public CustomerDTO getCustomerById(int id) {
        Optional<Customer> customer = customerRepository.findById(id);
        if(customer.isEmpty()) {
            throw new NoSuchElementException("There is no such an element!");
        }
        return customerMapper.toDTO(customer.get());
    }

//    private CustomerDTO convertEntityToDTO(Customer customer) {
//        return modelMapper.map(customer,CustomerDTO.class);
//    }
}
