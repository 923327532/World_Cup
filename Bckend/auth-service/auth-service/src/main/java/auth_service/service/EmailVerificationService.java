package auth_service.service;

import auth_service.entity.EmailVerificationToken;
import auth_service.entity.User;
import auth_service.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final TokenRepository tokenRepository;

    public String generateOtp() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }

    public EmailVerificationToken createToken(User user) {
        String otp = generateOtp();
        EmailVerificationToken token = new EmailVerificationToken();
        token.setUser(user);
        token.setToken(otp);
        token.setExpiresAt(LocalDateTime.now().plusMinutes(15));
        token.setUsed(false);

        return tokenRepository.save(token);
    }

    public boolean verifyToken(String email, String otp) {
        return tokenRepository.findByTokenAndUserEmail(otp, email)
            .filter(t -> !t.getUsed() && t.getExpiresAt().isAfter(LocalDateTime.now()))
            .map(t -> {
                t.setUsed(true);
                tokenRepository.save(t);
                return true;
            })
            .orElse(false);
    }
}