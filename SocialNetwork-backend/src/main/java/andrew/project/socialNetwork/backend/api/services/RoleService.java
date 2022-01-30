package andrew.project.socialNetwork.backend.api.services;

import andrew.project.socialNetwork.backend.api.constants.RoleName;
import andrew.project.socialNetwork.backend.api.entities.Role;

import java.util.List;

public interface RoleService {
    List<Role> findAll();

    Role findById(Long id);

    Role findByRoleName(RoleName roleName);

    Role save(Role role);

    void delete(Role role);
}
