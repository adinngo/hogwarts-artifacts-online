package com.example.hogwarts_artifacts_online.hogwartsUser;

import com.example.hogwarts_artifacts_online.system.exception.ObjectNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    UserService userService;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testFindAllSuccess() {
        HogwartsUser u = new HogwartsUser();
        u.setId(1L);
        u.setUsername("john");
        u.setPassword("123456");
        u.setEnabled(true);
        u.setRoles("admin user");

        HogwartsUser u2 = new HogwartsUser();
        u2.setId(2L);
        u2.setUsername("eric");
        u2.setPassword("654321");
        u2.setEnabled(true);
        u2.setRoles("user");

        HogwartsUser u3 = new HogwartsUser();
        u3.setId(3L);
        u3.setUsername("tom");
        u3.setPassword("qwerty");
        u3.setEnabled(false);
        u3.setRoles("user");

        List<HogwartsUser> users = List.of(u, u2, u3);

        given(this.userRepository.findAll()).willReturn(users);
        //when
        List<HogwartsUser> returnedUsers = this.userService.findAll();
        //then
        assertThat(returnedUsers.size()).isEqualTo(users.size());
        assertThat(returnedUsers.get(0).getId()).isEqualTo(users.get(0).getId());
        assertThat(returnedUsers.get(0).getUsername()).isEqualTo(users.get(0).getUsername());

        assertThat(returnedUsers.get(1).getId()).isEqualTo(users.get(1).getId());
        assertThat(returnedUsers.get(1).getUsername()).isEqualTo(users.get(1).getUsername());

        verify(this.userRepository, times(1)).findAll();

    }

    @Test
    void testFindByIdSuccess() {
        HogwartsUser u = new HogwartsUser();
        u.setId(1L);
        u.setUsername("john");
        u.setPassword("123456");
        u.setEnabled(true);
        u.setRoles("admin user");

        given(this.userRepository.findById(1L)).willReturn(Optional.of(u));

        HogwartsUser returnedUser = this.userService.findById(1L);

        assertThat(returnedUser.getId()).isEqualTo(u.getId());
        assertThat(returnedUser.getUsername()).isEqualTo(u.getUsername());
        assertThat(returnedUser.getPassword()).isEqualTo(u.getPassword());
        assertThat(returnedUser.getRoles()).isEqualTo(u.getRoles());

        verify(this.userRepository, times(1)).findById(1L);
    }

    @Test
    void testFindByIdNotFound() {
        given(this.userRepository.findById(1L)).willReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> this.userService.findById(1L));

        verify(this.userRepository, times(1)).findById(1L);
    }

    @Test
    void testSaveSuccess() {
        HogwartsUser u = new HogwartsUser();
        u.setId(1L);
        u.setUsername("john");
        u.setPassword("123456");
        u.setEnabled(true);
        u.setRoles("admin user");

        given(this.bCryptPasswordEncoder.encode(u.getPassword())).willReturn("123456");
        given(this.userRepository.save(u)).willReturn(u);

        HogwartsUser savedUser = this.userService.save(u);

        assertThat(savedUser.getId()).isEqualTo(u.getId());
        assertThat(savedUser.getUsername()).isEqualTo(u.getUsername());
        assertThat(savedUser.getRoles()).isEqualTo(u.getRoles());

        verify(this.userRepository, times(1)).save(u);
    }

    @Test
    void testUpdateByAdminSuccess() {
        HogwartsUser oldUser = new HogwartsUser();
        oldUser.setId(1L);
        oldUser.setUsername("john");
        oldUser.setPassword("123456");
        oldUser.setEnabled(true);
        oldUser.setRoles("admin user");

        HogwartsUser update = new HogwartsUser();
        update.setId(1L);
        update.setUsername("john-updated");
        update.setEnabled(true);
        update.setRoles("admin user");

        given(this.userRepository.findById(2L)).willReturn(Optional.of(oldUser));
        given(this.userRepository.save(oldUser)).willReturn(oldUser);

        HogwartsUser hogwartsUser = new HogwartsUser();
        hogwartsUser.setRoles("admin");
        MyUserPrincipal myUserPrincipal = new MyUserPrincipal(hogwartsUser);

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken(myUserPrincipal, null, myUserPrincipal.getAuthorities()));
        SecurityContextHolder.setContext(securityContext);

        HogwartsUser updatedUser = this.userService.update(2l, update);

        assertThat(updatedUser.getUsername()).isEqualTo(oldUser.getUsername());

        verify(this.userRepository, times(1)).findById(2L);
        verify(this.userRepository, times(1)).save(oldUser);
    }

    @Test
    void testUpdateByUserSuccess() {
        HogwartsUser oldUser = new HogwartsUser();
        oldUser.setId(2L);
        oldUser.setUsername("eric");
        oldUser.setPassword("654321");
        oldUser.setEnabled(true);
        oldUser.setRoles("user");

        HogwartsUser update = new HogwartsUser();
        update.setId(2L);
        update.setUsername("john-updated");
        update.setEnabled(true);
        update.setRoles("user");

        given(this.userRepository.findById(2L)).willReturn(Optional.of(oldUser));
        given(this.userRepository.save(oldUser)).willReturn(oldUser);

        HogwartsUser hogwartsUser = new HogwartsUser();
        hogwartsUser.setRoles("user");
        MyUserPrincipal myUserPrincipal = new MyUserPrincipal(hogwartsUser);

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken(myUserPrincipal, null, myUserPrincipal.getAuthorities()));
        SecurityContextHolder.setContext(securityContext);

        HogwartsUser updatedUser = this.userService.update(2l, update);

        assertThat(updatedUser.getUsername()).isEqualTo(oldUser.getUsername());

        verify(this.userRepository, times(1)).findById(2L);
        verify(this.userRepository, times(1)).save(oldUser);
    }

    @Test
    void testUpdateErrorWithNonExistenceId() {
        HogwartsUser u = new HogwartsUser();
        u.setId(1L);
        u.setUsername("john");
        u.setPassword("123456");
        u.setEnabled(true);
        u.setRoles("admin user");

        HogwartsUser update = new HogwartsUser();
        update.setId(1L);
        update.setUsername("john-updated");
        update.setEnabled(true);
        update.setRoles("admin user");

        given(this.userRepository.findById(Mockito.anyLong())).willReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class,
                () -> this.userService.update(Mockito.anyLong(), update));

        verify(this.userRepository, times(1)).findById(Mockito.anyLong());

    }

    @Test
    void testDeleteSuccess() {
        HogwartsUser u = new HogwartsUser();
        u.setId(1L);
        u.setUsername("john");
        u.setPassword("123456");
        u.setEnabled(true);
        u.setRoles("admin user");

        given(this.userRepository.findById(1L)).willReturn(Optional.of(u));
        doNothing().when(this.userRepository).deleteById(1l);

        this.userService.delete(1l);

        verify(this.userRepository, times(1)).findById(1l);
        verify(this.userRepository, times(1)).deleteById(1l);
    }

    @Test
    void testDeleteErrorWithNonExistenceId() {
        given(this.userRepository.findById(1L)).willReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> this.userService.delete(1l));

        verify(this.userRepository, times(1)).findById(1l);

    }
}