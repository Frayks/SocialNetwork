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
@Table(name = "users_chats")
public class UserChat {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false)
    private Long firstUserId;
    @Column(nullable = false)
    private Long firstUserNumOfUnreadMessages = 0L;
    @Column(nullable = false)
    private Long secondUserId = 0L;
    @Column(nullable = false)
    private Long secondUserNumOfUnreadMessages = 0L;


}
