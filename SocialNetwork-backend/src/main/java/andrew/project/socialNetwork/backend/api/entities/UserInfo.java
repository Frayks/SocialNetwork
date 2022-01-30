package andrew.project.socialNetwork.backend.api.entities;

import andrew.project.socialNetwork.backend.api.constants.Sex;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "users_info")
public class UserInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToOne(mappedBy = "userInfo")
    private User user;
    @Column(nullable = false)
    private String avatarUrl;
    @Column(nullable = false)
    private Timestamp dateOfBirth;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Sex sex;
    private String city;
    private String school;
    private String university;
    private String aboutYourself;

    public UserInfo() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Timestamp getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Timestamp dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public String getAboutYourself() {
        return aboutYourself;
    }

    public void setAboutYourself(String aboutYourself) {
        this.aboutYourself = aboutYourself;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "id=" + id +
                ", user=" + user +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", sex=" + sex +
                ", city='" + city + '\'' +
                ", school='" + school + '\'' +
                ", university='" + university + '\'' +
                ", aboutYourself='" + aboutYourself + '\'' +
                '}';
    }
}
