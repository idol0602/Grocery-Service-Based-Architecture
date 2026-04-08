package nguyennhatquan.springbootreview.security;

import lombok.RequiredArgsConstructor;
import nguyennhatquan.springbootreview.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .map(CustomUserDetails::fromUser)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }
}
