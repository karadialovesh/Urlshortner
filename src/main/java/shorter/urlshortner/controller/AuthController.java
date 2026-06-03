package shorter.urlshortner.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shorter.urlshortner.Dto.RegisterRequest;
import shorter.urlshortner.service.UserService;

import java.util.Map;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        userService.registerUser(request);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody RegisterRequest request) {
        String token = userService.loginUser(request);
        return ResponseEntity.ok(Map.of("token", token));
    }
    @GetMapping("/home")
    public ResponseEntity<String> home() {

        return ResponseEntity.ok("AWS is working fine. It is home page");
    }
}
