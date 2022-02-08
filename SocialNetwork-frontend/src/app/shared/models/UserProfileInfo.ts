import {UserPhoto} from "./UserPhoto";
import {UserPost} from "./UserPost";
import {UserFriend} from "./UserFriend";

export class UserProfileInfo {
  username!: string
  firstName!: string
  lastName!: string
  avatarUrl!: string
  dateOfBirth!: string
  sex!: string
  city!: string
  school!: string
  university!: string
  aboutYourself!: string
  userPhotoList!: UserPhoto[]
  userPostList!: UserPost[]
  userFriendList!: UserFriend[]
  friend!: boolean
  requestToFriends!: boolean
}
