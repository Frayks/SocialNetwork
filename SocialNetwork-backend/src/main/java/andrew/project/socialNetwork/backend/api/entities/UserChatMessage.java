package andrew.project.socialNetwork.backend.api.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.sql.Timestamp;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@Table(name = "users_chats_messages")
public class UserChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false)
    private Long chatId;
    @Column(nullable = false)
    private Long userId;
    @Column(nullable = false, length = 1000)
    private String text;
    @Column(nullable = false)
    private Timestamp creationTime;
    @Column(nullable = false)
    private Boolean revised = false;

}
