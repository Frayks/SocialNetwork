import {UserPhoto} from "./UserPhoto";
import {UserPost} from "./UserPost";
import {ShortUserInfo} from "./ShortUserInfo";

export class UserProfileInfo {
  id!: number
  username!: string
  firstName!: string
  lastName!: string
  avatarUri!: string
  dateOfBirth!: string
  sex!: string
  city!: string
  school!: string
  university!: string
  aboutYourself!: string
  userPhotoList!: UserPhoto[]
  userPostList!: UserPost[]
  userFriendList!: ShortUserInfo[]
  friend!: boolean
  requestToFriends!: boolean
}
