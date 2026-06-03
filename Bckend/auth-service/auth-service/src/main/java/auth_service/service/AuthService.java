package auth_service.service;

import auth_service.dto.AuthResponse;
import auth_service.dto.LoginRequest;
import auth_service.dto.RegisterRequest;
import auth_service.entity.Role;
import auth_service.entity.User;
import auth_service.repository.RoleRepository;
import auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailVerificationService emailVerificationService;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        Role role = roleRepository.findByName(request.getRole())
            .orElseThrow(() -> new RuntimeException("Role not found"));

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setRole(role);
        user.setEmailVerified(false);

        user = userRepository.save(user);

        // Generate and send OTP
        String otp = emailVerificationService.createToken(user).getToken();
        sendOtpEmail(request.getEmail(), otp);

        return new AuthResponse(null, null, user.getId(), user.getEmail(), role.getName());
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }

        if (!user.getEmailVerified()) {
            throw new RuntimeException("Email not verified");
        }

        String token = jwtService.generateToken(user.getId(), user.getEmail(), user.getRole().getName());

        return new AuthResponse(token, "Bearer", user.getId(), user.getEmail(), user.getRole().getName());
    }

    public boolean verifyEmail(String email, String token) {
        boolean verified = emailVerificationService.verifyToken(email, token);

        if (verified) {
            User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
            user.setEmailVerified(true);
            userRepository.save(user);
        }

        return verified;
    }

    private void sendOtpEmail(String email, String otp) {
        // Implement email sending logic
        System.out.println("Sending OTP " + otp + " to " + email);
    }
}