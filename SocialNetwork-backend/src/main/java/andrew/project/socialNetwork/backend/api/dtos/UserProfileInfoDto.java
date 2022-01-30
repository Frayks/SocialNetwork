package andrew.project.socialNetwork.backend.api.dtos;

import andrew.project.socialNetwork.backend.api.constants.Sex;

import java.util.Date;

public class UserProfileInfoDto {

    private String username;
    private String firstName;
    private String lastName;
    private String avatarUrl;
    private Date dateOfBirth;
    private Sex sex;
    private String city;
    private String school;
    private String university;
    private String aboutYourself;

    public UserProfileInfoDto() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
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
        return "UserProfileInfoDto{" +
                "username='" + username + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
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
