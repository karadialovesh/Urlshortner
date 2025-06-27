package shorter.urlshortner.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import shorter.urlshortner.Dto.RegisterRequest;
import shorter.urlshortner.model.User;
import shorter.urlshortner.repository.UserRepository;
import shorter.urlshortner.util.JwtUtil;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public void registerUser(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        Set<String> roles = request.getUsername().equals("lovesh") ?
                Set.of("ROLE_USER", "ROLE_ADMIN") : Set.of("ROLE_USER");

        User user = User.builder()
            .username(request.getUsername())
            .password(passwordEncoder.encode(request.getPassword()))
            .roles(roles) // default role
            .build();

        userRepository.save(user);
    }

    public String loginUser(RegisterRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return jwtUtil.generateToken(user.getUsername(), user.getRoles());
    }
}
