package com.example.hogwarts_artifacts_online.hogwartsUser;

import com.example.hogwarts_artifacts_online.system.exception.ObjectNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<HogwartsUser> findAll() {
        return this.userRepository.findAll();
    }

    public HogwartsUser findById(Long userId) {
        return this.userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("user", userId + ""));
    }

    public HogwartsUser save(HogwartsUser newUser) {
        return this.userRepository.save(newUser);
    }

    public HogwartsUser update(Long userId, HogwartsUser updated) {
        return this.userRepository.findById(userId).map((user) -> {
             user.setUsername(updated.getUsername());
             user.setEnabled(updated.getEnabled());
             user.setRoles(updated.getRoles());
             return this.userRepository.save(user);
        }).orElseThrow(() -> new ObjectNotFoundException("user", userId + ""));
    }

    public void delete(Long userId) {
        this.userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("user", userId + ""));
        this.userRepository.deleteById(userId);
    }

}
