package com.example.demoauth.controller;


import com.example.demoauth.configs.JwtUtils;
import com.example.demoauth.models.ERole;
import com.example.demoauth.models.Role;
import com.example.demoauth.models.User;
import com.example.demoauth.pojo.JwtResponse;
import com.example.demoauth.pojo.LoginRequest;
import com.example.demoauth.pojo.MessageResponse;
import com.example.demoauth.pojo.SignupRequest;
import com.example.demoauth.repos.RoleRepo;
import com.example.demoauth.repos.UserRepo;
import com.example.demoauth.services.UserDetailsImpl;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserRepo              userRepo;
    private final RoleRepo              roleRepo;
    private final PasswordEncoder       passwordEncoder;
    private final JwtUtils              jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<?> authUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        Set<String> roles = userDetails
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        return ResponseEntity.ok(new JwtResponse(
                jwt,
                userDetails.getId(),
                userDetails.getEmail(),
                userDetails.getUsername(),
                roles)
        );
    }


    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signupRequest) {

        if (userRepo.existsByUsername(signupRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("User already exist"));
        }

        if (userRepo.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("User with such email already exist"));
        }

        Set<String> reqRoles = signupRequest.getRoles();

        Set<Role> roles = new HashSet<>();

        if (reqRoles == null) {
            Role userRole = roleRepo.findByName(ERole.ROLE_USER).orElseThrow(() -> new RuntimeException("Error, Role USER not found"));
            roles.add(userRole);
        } else {
            reqRoles.forEach(role -> {
                switch (role) {
                    case "ROLE_ADMIN":
                        Role adminRole = roleRepo.findByName(ERole.ROLE_ADMIN).orElseThrow(() -> new RuntimeException("Role admin not exist"));
                        roles.add(adminRole);
                    case "ROLE_MODERATOR":
                        Role moderatorRole = roleRepo.findByName(ERole.ROLE_ADMIN).orElseThrow(() -> new RuntimeException("Role moderator not exist"));
                        roles.add(moderatorRole);
                    default:
                        Role userRole = roleRepo.findByName(ERole.ROLE_USER).orElseThrow(() -> new RuntimeException("Role moderator not exist"));
                        roles.add(userRole);
                }
            });
        }

        User user = new User(
                signupRequest.getUsername(),
                signupRequest.getEmail(),
                passwordEncoder.encode(signupRequest.getPassword()),
                roles
        );

        userRepo.save(user);

        return ResponseEntity.ok(user);
    }
}
