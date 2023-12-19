package com.example.demoauth.pojo;


import com.example.demoauth.models.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {
    private String token;

    // default token type
    private String tokenType = "Bearer";

    // payload data
    private Long        id;
    private String      username;
    private String      email;
    private Set<String> roles;

    public JwtResponse(String token, Long id, String username, String email, Set<String> roles) {
        this.token    = token;
        this.id       = id;
        this.username = username;
        this.email    = email;
        this.roles    = roles;
    }
}
