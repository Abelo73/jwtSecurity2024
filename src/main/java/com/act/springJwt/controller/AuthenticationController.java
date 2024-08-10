package com.act.springJwt.controller;

import com.act.springJwt.dto.UserDTO;
import com.act.springJwt.model.AuthenticationResponse;
import com.act.springJwt.model.User;
import com.act.springJwt.service.AuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AuthenticationController {

    private final static Logger logger = LoggerFactory.getLogger(AuthenticationService.class);
    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }



    @GetMapping("/all")
    public ResponseEntity<List<User>> allUsers(){
        List<User> users = authenticationService.users();
        logger.info("All Users from Controller: {}", users);
        return ResponseEntity.ok(users);
    }

//    @GetMapping("user/{id}")
//    public ResponseEntity<UserDTO> findById(@PathVariable Long id){
//        return ResponseEntity.ok(authenticationService.findById(id));
//    }


    @GetMapping("/hello")
    public String sayHello(){
        return "Hello, from Secured";
    }
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody User request){
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody User request){
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }


    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers() {
        try {
            List<User> users = authenticationService.users();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Integer id) {
        try {
            UserDTO userDTO = authenticationService.findById(id);
            return ResponseEntity.ok(userDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        try {
            authenticationService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }


    @PostMapping("/reset-password")
    public ResponseEntity<String> requestPasswordReset(@RequestParam String email) {
        try {
            authenticationService.sendPasswordResetToken(email);
            return ResponseEntity.ok("Password reset token sent to email.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/reset-password/{token}")
    public ResponseEntity<String> resetPassword(@PathVariable String token, @RequestBody String newPassword) {
        try {
            authenticationService.resetPassword(token, newPassword);
            return ResponseEntity.ok("Password reset successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

}
