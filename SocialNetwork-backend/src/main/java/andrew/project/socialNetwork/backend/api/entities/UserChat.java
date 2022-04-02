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
    @Column(nullable = false, columnDefinition = "integer default 0")
    private Long firstUserNumOfUnreadMessages;
    @Column(nullable = false)
    private Long secondUserId;
    @Column(nullable = false, columnDefinition = "integer default 0")
    private Long secondUserNumOfUnreadMessages;


}
