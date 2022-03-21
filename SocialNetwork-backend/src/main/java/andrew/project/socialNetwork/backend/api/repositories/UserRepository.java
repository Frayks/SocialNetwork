package andrew.project.socialNetwork.backend.api.repositories;

import andrew.project.socialNetwork.backend.api.entities.User;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);

    User findByEmail(String email);

    @Query("FROM User u WHERE (LOWER(u.firstName) LIKE LOWER(?1) OR LOWER(u.lastName) LIKE LOWER(?1) OR u.username LIKE ?1) AND u.username NOT LIKE ?2")
    List<User> findUsersByText(String text, String mainUsername, PageRequest pageRequest);

    @Query("FROM User u WHERE (LOWER(u.firstName) LIKE LOWER(?1) AND LOWER(u.lastName) LIKE LOWER(?2)) AND u.username NOT LIKE ?2")
    List<User> findUsersByFirstNameAndLastName(String firstName, String lastName, String mainUsername, PageRequest pageRequest);

    List<User> findByIdIn(List<Long> idList);

}
