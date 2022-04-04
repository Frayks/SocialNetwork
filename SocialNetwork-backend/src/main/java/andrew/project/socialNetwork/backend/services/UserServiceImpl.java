package andrew.project.socialNetwork.backend.services;

import andrew.project.socialNetwork.backend.api.constants.RoleName;
import andrew.project.socialNetwork.backend.api.entities.Role;
import andrew.project.socialNetwork.backend.api.entities.User;
import andrew.project.socialNetwork.backend.api.repositories.RoleRepository;
import andrew.project.socialNetwork.backend.api.repositories.UserRepository;
import andrew.project.socialNetwork.backend.api.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService, UserDetailsService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User with username (" + username + ") not found!");
        }
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        user.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority(role.getRoleName().name())));
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public List<User> findByIdIn(List<Long> idList) {
        return userRepository.findByIdIn(idList);
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<User> findUsersByText(String text, String mainUsername, int limit) {
        return userRepository.findUsersByText(text, mainUsername, PageRequest.of(0, limit));
    }

    @Override
    public List<User> findUsersByFirstNameAndLastName(String firstName, String lastName, String mainUsername, int limit) {
        return userRepository.findUsersByFirstNameAndLastName(firstName, lastName, mainUsername, PageRequest.of(0, limit));
    }

    @Override
    public User save(User user) {
        user.setRegistrationTime(new Timestamp(System.currentTimeMillis()));
        return userRepository.save(user);
    }

    @Override
    public void delete(User user) {
        userRepository.delete(user);
    }

    @Override
    public void addRoleToUser(String username, RoleName roleName) {
        User user = userRepository.findByUsername(username);
        Role role = roleRepository.findByRoleName(roleName);
        user.getRoles().add(role);
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setRoleRepository(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

}
