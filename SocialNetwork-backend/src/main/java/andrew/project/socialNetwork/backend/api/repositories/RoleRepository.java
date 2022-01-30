package andrew.project.socialNetwork.backend.api.repositories;

import andrew.project.socialNetwork.backend.api.constants.RoleName;
import andrew.project.socialNetwork.backend.api.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByRoleName(RoleName roleName);

}
