package com.Ayan.Mondal.VOTEONN.CONTROLLER;

import com.Ayan.Mondal.VOTEONN.DTO.LoginDTO;
import com.Ayan.Mondal.VOTEONN.DTO.UserDTO;
import com.Ayan.Mondal.VOTEONN.MODEL.UserEntity;
import com.Ayan.Mondal.VOTEONN.SERVICE.UserService;
import com.mysql.cj.x.protobuf.Mysqlx;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
   private UserService userService;



//    @Autowired
//    private AuthenticationManager authenticationManager;

    @PostMapping("/register/user")
    public ResponseEntity<?> RegisterUser(@RequestBody UserDTO user){
        return ResponseEntity.status(HttpStatus.OK).body(userService.registerAsUser(user));
    }

    @PostMapping("/register/admin")
    public ResponseEntity<?> RegisterAdmin(@RequestBody UserDTO user){
        return ResponseEntity.status(HttpStatus.OK).body(userService.registerAsAdmin(user));
    }


//    @PostMapping("/Login")
//    public ResponseEntity<?> UserLogin(@RequestBody LoginDTO dto){
//        try {
//            Authentication authentication = authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
//            );
//            return ResponseEntity.ok("Login successful");
//        } catch (AuthenticationException ex) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
//        }
//
//    }


    @GetMapping("/all/user")
    public ResponseEntity<?> getAllUsers(){
        return ResponseEntity.status(HttpStatus.OK).body(userService.getAllUser());

    }
}
