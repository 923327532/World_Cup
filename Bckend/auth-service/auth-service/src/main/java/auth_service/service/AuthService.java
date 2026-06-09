package auth_service.service;

import auth_service.dto.AuthResponse;
import auth_service.dto.LoginRequest;
import auth_service.dto.RegisterRequest;
import auth_service.entity.Role;
import auth_service.entity.User;
import auth_service.repository.RoleRepository;
import auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailVerificationService emailVerificationService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El correo ya está registrado");
        }

        String roleName = request.getRole().trim().toUpperCase(Locale.ROOT);
        String studentCode = normalizeStudentCode(request.getStudentCode());

        if ("STUDENT".equals(roleName) && studentCode == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El código estudiantil es obligatorio para estudiantes");
        }

        if (!"STUDENT".equals(roleName)) {
            studentCode = null;
        }

        if (studentCode != null && userRepository.existsByStudentCode(studentCode)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El código estudiantil ya está en uso");
        }

        Role role = roleRepository.findByName(roleName)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rol inválido"));

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setStudentCode(studentCode);
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
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
        }

        if (!user.getEmailVerified()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Correo no verificado");
        }

        String token = jwtService.generateToken(user.getId(), user.getEmail(), user.getRole().getName());

        return new AuthResponse(token, "Bearer", user.getId(), user.getEmail(), user.getRole().getName());
    }

    public boolean verifyEmail(String email, String token) {
        boolean verified = emailVerificationService.verifyToken(email, token);

        if (!verified) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Código de verificación inválido o expirado");
        }

        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        user.setEmailVerified(true);
        userRepository.save(user);

        return true;
    }

    private void sendOtpEmail(String email, String otp) {
        // Implement email sending logic
        System.out.println("Sending OTP " + otp + " to " + email);
    }

    private String normalizeStudentCode(String studentCode) {
        if (studentCode == null) {
            return null;
        }
        String normalized = studentCode.trim();
        return normalized.isEmpty() ? null : normalized;
    }
}