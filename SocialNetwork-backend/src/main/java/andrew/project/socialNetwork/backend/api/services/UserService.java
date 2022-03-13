package andrew.project.socialNetwork.backend.api.services;

import andrew.project.socialNetwork.backend.api.constants.RoleName;
import andrew.project.socialNetwork.backend.api.entities.User;

import java.util.List;

public interface UserService {
    List<User> findAll();

    List<User> findByIds(List<Long> userIdList);

    User findByUsername(String username);

    User findById(Long id);

    User findByEmail(String email);

    User save(User user);

    void delete(User user);

    void addRoleToUser(String username, RoleName roleName);
}
