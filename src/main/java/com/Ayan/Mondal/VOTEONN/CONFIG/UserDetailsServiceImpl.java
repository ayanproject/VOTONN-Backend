package com.Ayan.Mondal.VOTEONN.CONFIG;

import com.Ayan.Mondal.VOTEONN.MODEL.UserEntity;
import com.Ayan.Mondal.VOTEONN.REPOSITORY.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // We use username as email in your app
        UserEntity user = userRepository.findByEmail(username) // <-- ASSUMING you have this method
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));

        // You can add user roles here (e.g., user.getRoles())
        return new User(user.getEmail(), user.getPassword(), new ArrayList<>());
    }
}