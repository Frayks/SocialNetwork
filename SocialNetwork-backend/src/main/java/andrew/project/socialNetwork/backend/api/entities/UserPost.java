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
@Table(name = "users_posts")
public class UserPost {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false)
    private Long userId;
    private String photoUrl;
    private String text;
    @Column(nullable = false, columnDefinition = "integer default 0")
    private Integer numOfLikes;

}
