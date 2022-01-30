package andrew.project.socialNetwork.backend.api.entities;

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
@Table(name = "users_photos")
public class UserPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false)
    private Long userId;
    @Column(nullable = false)
    private String photoUrl;
    @Column(nullable = false, columnDefinition = "integer default 0")
    private Integer numOfLikes;

}
