package fu.se.beind.Service;

import fu.se.beind.Dto.request.LoginRequest;
import fu.se.beind.Dto.request.RegisterRequest;
import fu.se.beind.Dto.response.AuthResponse;
import fu.se.beind.Entity.User;
import fu.se.beind.Entity.Role;
import fu.se.beind.Repo.RoleRepository;
import fu.se.beind.Repo.UserRepository;
import fu.se.beind.Security.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;
    private final AuthenticationManager authenticationManager;

    public AuthResponse login(LoginRequest request) {
        // Xác thực email + password
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Lấy user từ DB để trả thêm thông tin
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtTokenUtil.generateToken(user.getEmail());

        return new AuthResponse(
                token,
                user.getEmail(),
                user.getFullName(),
                user.getRole().getName()
        );
    }

    public AuthResponse register(RegisterRequest request) {
        // Kiểm tra email đã tồn tại chưa
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email đã tồn tại");
        }

        // Lấy role CUSTOMER mặc định
        Role role = roleRepository.findByName("CUSTOMER")
                .orElseThrow(() -> new RuntimeException("Role CUSTOMER not found"));

        // Tạo user mới
        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setPhoneNumber(request.getPhone());  // ← phone thay vì phoneNumber
        user.setRole(role);
        user.setIsActive(true);

        userRepository.save(user);

        String token = jwtTokenUtil.generateToken(user.getEmail());

        return new AuthResponse(
                token,
                user.getEmail(),
                user.getFullName(),
                user.getRole().getName()
        );
    }
}