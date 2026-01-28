package org.learncode.aama.controllers;

import org.learncode.aama.Dao.LoanRepo;
import org.learncode.aama.Dao.UserRepo;
import org.learncode.aama.Dto.*;
import org.learncode.aama.entites.UserPrincipal;
import org.learncode.aama.entites.Users;
import org.learncode.aama.service.jwtService;
import org.learncode.aama.service.userService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class Usercontroller {
    @Autowired
    private userService UserService;
    @Autowired
    private LoanRepo loanRepo;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private jwtService jwtService;
    @Autowired
    private UserRepo userRepo;

    @PostMapping("/register")
    public Users registerUser(@RequestBody Users users) {
        Users users1 = UserService.saveUser(users);
        return users1;
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody userDto loginDto) {
        try {
            // Get user by name
            Users user = userRepo.findUsersByName(loginDto.getName());

            if (user == null) {
                return ResponseEntity.status(401).body("User not found");
            }

            // Authenticate using userID and password
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.getUserID().toString(),
                            loginDto.getPassword()));

            if (authentication.isAuthenticated()) {
                // Generate JWT token
                String token = jwtService.getToken(user);

                // Return token
                HashMap<String, String> response = new HashMap<>();
                response.put("token", token);
                response.put("message", "Login successful");
                response.put("role", user.getRole());
                response.put("name", user.getName());

                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(401).body("Login Failed");
            }
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Login Failed: " + e.getMessage());
        }
    }

    @GetMapping("/dashboard")
    public MemberDashboardDto dashboard() {
        // Get authenticated user from JWT
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        Users user = principal.getUser();

        // Return user's dashboard stats
        return UserService.getMemberDashboardStats(user.getUserID());
    }

    @GetMapping("/contacts")
    @PreAuthorize("hasRole('ADMIN')")
    public ContactResponseDto getContacts() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        return UserService.getAllContacts(principal.getUser().getUserID());
    }
    @GetMapping("/member-contacts")
    public List<ContactFMemDto> getContactsformem() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        return UserService.getAllContactsFormem(principal.getUser().getUserID());
    }

}
