package com.example.manage.authController;

import com.example.manage.config.JwtService;
import com.example.manage.exception.EmailExistException;
import com.example.manage.exception.ExceptionHandling;
import com.example.manage.model.Role;
import com.example.manage.model.User;
import com.example.manage.repository.user.UserRepository;
import com.example.manage.service.user.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthenticationService extends ExceptionHandling {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserServiceImpl userService;


    public AuthenticationResponse register(RegisterRequest request) throws EmailExistException {
        var userExist = repository.findByEmail(request.getEmail());
        if(userExist.isPresent()){
            throw new EmailExistException("email is already exist");
        }
        var user = User.builder()
                .name(request.getName())
                .contactName(request.getContactName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .lockTime(new Date())
                .status(false)
                .build();
        repository.save(user);
        var jwtToken =jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .user(user)
                .build();

    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = repository.findByEmail(request.getEmail()).orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .user(user)
                .build();
    }

}
