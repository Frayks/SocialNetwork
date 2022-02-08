package andrew.project.socialNetwork.backend;

import andrew.project.socialNetwork.backend.api.constants.RoleName;
import andrew.project.socialNetwork.backend.api.constants.Sex;
import andrew.project.socialNetwork.backend.api.entities.*;
import andrew.project.socialNetwork.backend.api.services.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class SocialNetworkBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(SocialNetworkBackendApplication.class, args);
    }


    //@Bean
    CommandLineRunner run(UserService userService, RoleService roleService, UserPhotoService userPhotoService, UserPostService userPostService) {
        return args -> {
            Role role1 = new Role();
            role1.setRoleName(RoleName.USER);
            roleService.save(role1);

            Role role2 = new Role();
            role2.setRoleName(RoleName.ADMIN);
            roleService.save(role2);

            User user1 = new User();
            user1.setFirstName("Андрій");
            user1.setLastName("Дребот");
            user1.setUsername("and_you");
            user1.setPassword("1");
            user1.setEmail("andrii@gmail.com");
            user1.setRegistrationTime(new Timestamp(System.currentTimeMillis()));


            User user2 = new User();
            user2.setFirstName("Іван");
            user2.setLastName("Вінницький");
            user2.setUsername("vinnytile");
            user2.setPassword("1");
            user2.setEmail("vinnytile@gmail.com");
            user2.setRegistrationTime(new Timestamp(System.currentTimeMillis()));

            DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

            UserInfo userInfo1 = new UserInfo();
            userInfo1.setAvatarUrl("https://sun6-21.userapi.com/s/v1/if1/tnyBZ8EAmFn51Oe2bGHqgqwDsayH2lRVEnuKrEEaagW8yjhSPsVUmQjSPeeRSV1D2AKTV5RZ.jpg?size=200x200&quality=96&crop=269,0,1036,1036&ava=1");
            userInfo1.setSex(Sex.MALE);
            userInfo1.setCity("Трускавець");
            userInfo1.setAboutYourself("Java Developer");
            userInfo1.setSchool("НВК \"СЗШ №2-гімназія\"");
            userInfo1.setUniversity("ЛНУ ім. Івана Франка");
            userInfo1.setDateOfBirth(new Timestamp(dateFormat.parse("07.05.2001").getTime()));

            UserInfo userInfo2 = new UserInfo();
            userInfo2.setAvatarUrl("https://blog.cpanel.com/wp-content/uploads/2019/08/user-01.png");
            userInfo2.setSex(Sex.MALE);
            userInfo2.setDateOfBirth(new Timestamp(dateFormat.parse("07.05.2001").getTime()));

            user1.setUserInfo(userInfo1);
            user2.setUserInfo(userInfo2);

            user1 = userService.save(user1);
            user2 = userService.save(user2);

            UserPhoto userPhoto1 = new UserPhoto();
            userPhoto1.setPhotoUrl("https://img.freepik.com/free-photo/landscape-of-morning-fog-and-mountains-with-hot-air-balloons-at-sunrise_335224-794.jpg?size=626&ext=jpg");
            userPhoto1.setNumOfLikes(0);
            userPhoto1.setUserId(user1.getId());
            userPhoto1.setLoadTime(new Timestamp(System.currentTimeMillis()));

            UserPhoto userPhoto2 = new UserPhoto();
            userPhoto2.setPhotoUrl("https://st2.depositphotos.com/3651191/6922/i/600/depositphotos_69225679-stock-photo-mountains-with-pink-flowers.jpg");
            userPhoto2.setNumOfLikes(0);
            userPhoto2.setUserId(user1.getId());
            userPhoto2.setLoadTime(new Timestamp(System.currentTimeMillis()));

            UserPhoto userPhoto3 = new UserPhoto();
            userPhoto3.setPhotoUrl("https://img.freepik.com/free-photo/landscape-of-morning-fog-and-mountains-with-hot-air-balloons-at-sunrise_335224-794.jpg?size=626&ext=jpg");
            userPhoto3.setNumOfLikes(0);
            userPhoto3.setUserId(user1.getId());
            userPhoto3.setLoadTime(new Timestamp(System.currentTimeMillis()));

            UserPhoto userPhoto4 = new UserPhoto();
            userPhoto4.setPhotoUrl("https://st2.depositphotos.com/3651191/6922/i/600/depositphotos_69225679-stock-photo-mountains-with-pink-flowers.jpg");
            userPhoto4.setNumOfLikes(0);
            userPhoto4.setUserId(user1.getId());
            userPhoto4.setLoadTime(new Timestamp(System.currentTimeMillis()));

            userPhotoService.save(userPhoto1);
            userPhotoService.save(userPhoto2);
            userPhotoService.save(userPhoto3);
            userPhotoService.save(userPhoto4);

            UserPost userPost1 = new UserPost();
            userPost1.setPhotoUrl("https://img.freepik.com/free-photo/landscape-of-morning-fog-and-mountains-with-hot-air-balloons-at-sunrise_335224-794.jpg?size=626&ext=jpg");
            userPost1.setText("Пост1");
            userPost1.setNumOfLikes(0);
            userPost1.setUserId(user1.getId());
            userPost1.setCreationTime(new Timestamp(System.currentTimeMillis()));

            UserPost userPost2 = new UserPost();
            userPost2.setPhotoUrl("https://img.freepik.com/free-photo/landscape-of-morning-fog-and-mountains-with-hot-air-balloons-at-sunrise_335224-794.jpg?size=626&ext=jpg");
            userPost2.setText("Пост2");
            userPost2.setNumOfLikes(0);
            userPost2.setUserId(user1.getId());
            userPost2.setCreationTime(new Timestamp(System.currentTimeMillis()));

            userPostService.save(userPost1);
            userPostService.save(userPost2);

            userService.addRoleToUser("and_you", RoleName.USER);
            userService.addRoleToUser("and_you", RoleName.ADMIN);

            userService.addRoleToUser("vinnytile", RoleName.ADMIN);
        };
    }

    //@Bean
    CommandLineRunner run1(FriendsService friendsService, UserService userService) {
        return args -> {
            System.out.println(friendsService.findFriends(367L, 1));
            System.out.println(friendsService.findRequestsToFriends(367L));
            System.out.println(friendsService.checkIfFriends(367L, 369L));
            System.out.println(friendsService.checkIfFriends(369L, 367L));
            List<Long> userIdList = new ArrayList<>();
            userIdList.add(367L);
            userIdList.add(369L);
            List<User> userList = userService.findByIds(userIdList);
            System.out.println();
        };
    }

}
