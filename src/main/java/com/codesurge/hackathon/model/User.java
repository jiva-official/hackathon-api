package com.codesurge.hackathon.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User implements UserDetails {

    @Id
    private String id;

    @NotBlank(message = "Team name is required")
    @Indexed(unique = true)
    private String teamName;

    @NotBlank(message = "username is required")
    @Indexed(unique = true)
    private String username;

    @NotBlank(message = "Password is required")
    private String password;

    @Valid
    @Size(min = 4, max = 5, message = "Team must have 4-5 members")
    private List<TeamMember> teamMembers = new ArrayList<>();

    @NotBlank(message = "email is required")
    @Indexed(unique = true)
    private String email;

    private String assignedProblemId;

    private String submissionUrl;

    private String hostedUrl;

    private boolean solutionSubmitted = false;

    @Builder.Default
    private String role = "ROLE_USER";

    @Builder.Default
    private boolean isEnabled = true;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getUsername() {
        return teamName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }
}