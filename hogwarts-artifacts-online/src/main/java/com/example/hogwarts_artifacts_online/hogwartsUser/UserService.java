package com.example.hogwarts_artifacts_online.hogwartsUser;

import com.example.hogwarts_artifacts_online.system.exception.ObjectNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserService(UserRepository userRepository,
                       BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public List<HogwartsUser> findAll() {
        return this.userRepository.findAll();
    }

    public HogwartsUser findById(Long userId) {
        return this.userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("user", userId + ""));
    }

    public HogwartsUser save(HogwartsUser newUser) {
        newUser.setPassword(this.bCryptPasswordEncoder.encode(newUser.getPassword()));
        return this.userRepository.save(newUser);
    }

    public HogwartsUser update(Long userId, HogwartsUser updated) {
        return this.userRepository.findById(userId).map((user) -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if(authentication.getAuthorities().stream().noneMatch(grantedAuthority -> grantedAuthority.getAuthority().contains("admin") )) {
                user.setUsername(updated.getUsername());
            } else {
                user.setUsername(updated.getUsername());
                user.setRoles(updated.getRoles());
                user.setEnabled(updated.getEnabled());
            }
             return this.userRepository.save(user);
        }).orElseThrow(() -> new ObjectNotFoundException("user", userId + ""));
    }

    public void delete(Long userId) {
        this.userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("user", userId + ""));
        this.userRepository.deleteById(userId);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.userRepository.findByUsername(username)
                .map(foundUser -> new MyUserPrincipal(foundUser))
                .orElseThrow(() -> new UsernameNotFoundException("username " + username + " is not found"));
    }
}
