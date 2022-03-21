package andrew.project.socialNetwork.backend.api.services;

import andrew.project.socialNetwork.backend.api.constants.RoleName;
import andrew.project.socialNetwork.backend.api.entities.User;

import java.util.List;

public interface UserService {
    List<User> findAll();

    List<User> findByIdIn(List<Long> idList);

    User findByUsername(String username);

    User findById(Long id);

    User findByEmail(String email);

    List<User> findUsersByText(String text, String mainUsername, int limit);

    List<User> findUsersByFirstNameAndLastName(String firstName, String lastName, String mainUsername, int limit);

    User save(User user);

    void delete(User user);

    void addRoleToUser(String username, RoleName roleName);
}
