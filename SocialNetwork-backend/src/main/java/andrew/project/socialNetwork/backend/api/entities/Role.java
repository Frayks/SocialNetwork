package andrew.project.socialNetwork.backend.api.entities;

import andrew.project.socialNetwork.backend.api.constants.RoleName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RoleName roleName;

}
