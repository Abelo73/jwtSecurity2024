package com.act.springJwt.service;

import com.act.springJwt.dto.UserDTO;
import com.act.springJwt.exception.Exception;
import com.act.springJwt.model.AuthenticationResponse;
import com.act.springJwt.model.User;
import com.act.springJwt.repository.UserRepository;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
public class AuthenticationService {

    private final static Logger logger = LoggerFactory.getLogger(AuthenticationService.class);
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    private final EmailService emailService;
    private JavaMailSender mailSender;


    public AuthenticationService(UserRepository repository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager, EmailService emailService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.emailService = emailService;
    }

    public AuthenticationResponse register(User request){

        Optional<User> existingUser = repository.findByUsername(request.getUsername());
        if (existingUser.isPresent()){
            throw new IllegalArgumentException("Username is already taken");
        }

        validateUser(request);

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user = repository.save(user);
        String token = jwtService.generateToke(user);

        return new AuthenticationResponse(token);

    }

    public void validateUser(User user){
        if (user.getFirstName().isEmpty() || user.getFirstName() ==null){
            throw new Exception.ValidationException("FirstName can not be empty");
        } else if (user.getLastName().isEmpty() || user.getLastName() ==null) {
            throw new Exception.ValidationException("Last name can not be empty");
        } else if (user.getUsername().isEmpty() || user.getUsername() == null) {
            throw new Exception.ValidationException("Username can not be empty");
        } else if (user.getPassword().isEmpty() || user.getUsername() ==null) {
            throw new Exception.ValidationException("Password can not be empty");
        }
    }

    public AuthenticationResponse authenticate(User request){

        validateLogin(request);

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword()
            ));
        }catch (java.lang.Exception e){
            throw new Exception.AuthenticationFailedException("Invalid username or password");
        }


        User user = repository.findByUsername(request.getUsername()).orElseThrow();
        String token = jwtService.generateToke(user);

        return new AuthenticationResponse(token);
    }

    public void validateLogin(User user){
        if (user.getUsername() == null || user.getUsername().isEmpty()){
            throw new Exception.ValidationException("Username can not be empty");
        }
        if (user.getPassword() ==  null || user.getPassword().isEmpty()){
            throw new Exception.ValidationException("Password can not be empty");
        }
    }

    public List<User> users(){
        List<User> users = repository.findAll();
        logger.info("Fetched Uses: ", users);
        return users;
    }



    public UserDTO findById(Integer id) {
        Optional<User> existingUser = repository.findById(id);
        if (existingUser.isPresent()) {
            return convertToDTO(existingUser.get());
        } else {
            throw new RuntimeException("User not found");
        }
    }



    public void deleteUser(Integer id) {
        Optional<User> user = repository.findById(id);
        if (user.isPresent()) {
            repository.deleteById(id);
            logger.info("User with ID {} deleted successfully", id);
        } else {
            throw new Exception.ValidationException("User not found");
        }
    }


    private UserDTO convertToDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setUsername(user.getUsername());
        userDTO.setRole(user.getRole());
        return userDTO;
    }


    public void sendPasswordResetToken(String email) {
        User user = repository.findByUsername(email).orElseThrow(() -> new RuntimeException("User not found"));

//        String token = jwtService.generatePasswordResetToken(user); // Generate reset token
        String token = jwtService.generateToke(user);
        logger.info("Generated password reset token for user {}: {}", email, token);
//        emailService.sendPasswordResetEmail(email, token);


        // Send token to user via email (this part is not included in the example)

        logger.info("Generated password reset token for user {}: {}", email, token);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Password Reset Request");
        message.setText("Here is your password reset token: " + token);

        emailService.sendEmail(email, token);
        logger.info("Password reset token sent to user {}.", email);
        // You need an email service to actually send the email
    }


//    public void resetPassword(String token, String newPassword) {
//
//    public void resetPassword(String token, String newPassword) {
//        if (jwtService.isTokenIsExpired(token)) {
//            throw new RuntimeException("Token is expired");
//        }
//
//        Claims claims;
//        // Correctly extract claims using a Function
//        claims = jwtService.extractClaims(token, Function.identity());
//
//        String username = claims.getSubject();
//        if (username == null) {
//            throw new RuntimeException("Invalid token claims");
//        }
//
//        User user = repository.findByUsername(username).orElseThrow(() ->
//                new RuntimeException("User not found"));
//
//        user.setPassword(passwordEncoder.encode(newPassword));
//        repository.save(user);
//    }

//
//    public void resetPassword(String token, String newPassword) {
//        logger.info("Received password reset request with token: {}", token);
//
//        if (jwtService.isTokenIsExpired(token)) {
//            logger.error("Password reset token is expired.");
//            throw new RuntimeException("Token is expired");
//        }
//
//        Claims claims;
//        // Extract claims using a Function
//        claims = jwtService.extractClaims(token, Function.identity());
//
//        String username = claims.getSubject();
//        if (username == null) {
//            logger.error("Username extracted from token is null.");
//            throw new RuntimeException("Invalid token claims");
//        }
//
//        User user;
//        user = repository.findByUsername(username).orElseThrow(() -> {
//            logger.error("User with username {} not found.", username);
//            return new RuntimeException("User not found");
//        });
//
//        user.setPassword(passwordEncoder.encode(newPassword));
//        repository.save(user);
//        logger.info("Password for user with username {} has been reset successfully.", username);
//    }
public void resetPassword(String token, String newPassword) {
    logger.info("Received password reset request with token: {}", token);

    if (jwtService.isTokenIsExpired(token)) {
        logger.error("Password reset token is expired.");
        throw new RuntimeException("Token is expired");
    }

    Claims claims = jwtService.extractClaims(token, Function.identity());
    String username = claims.getSubject();
    logger.info("Username extracted from token: {}", username);

    if (username == null) {
        logger.error("Username extracted from token is null.");
        throw new RuntimeException("Invalid token claims");
    }

    User user = repository.findByUsername(username).orElseThrow(() -> {
        logger.error("User with username {} not found.", username);
        return new RuntimeException("User not found");
    });

    // Encode and set only the new password
    String encodedPassword = passwordEncoder.encode(newPassword);
    logger.debug("New Encrypted Password: {}", encodedPassword);

    user.setPassword(encodedPassword);
    repository.save(user);
    logger.info("Password for user with username {} has been reset successfully.", username);
}

//    public void sendPasswordResetToken(String  email){
//        User user = repository.findByUsername(email).orElseThrow(()->new RuntimeException("User not Found"));
//    }

}
