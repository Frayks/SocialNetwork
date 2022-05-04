package andrew.project.socialNetwork.backend.api.entities;

import andrew.project.socialNetwork.backend.api.constants.Sex;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users_info")
public class UserInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToOne(mappedBy = "userInfo")
    private User user;
    @Column(nullable = false)
    private String avatarName;
    private Timestamp dateOfBirth;
    @Enumerated(EnumType.STRING)
    private Sex sex;
    private String city;
    private String school;
    private String university;
    private String aboutYourself;

    @Override
    public String toString() {
        return "UserInfo{" +
                "id=" + id +
                ", avatarName='" + avatarName + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", sex=" + sex +
                ", city='" + city + '\'' +
                ", school='" + school + '\'' +
                ", university='" + university + '\'' +
                ", aboutYourself='" + aboutYourself + '\'' +
                '}';
    }
}
