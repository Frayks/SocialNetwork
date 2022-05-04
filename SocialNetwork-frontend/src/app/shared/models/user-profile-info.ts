import {UserPhoto} from "./user-photo";
import {ShortUserInfo} from "./short-user-info";

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
  numOfPosts!: number
  userPhotoList!: UserPhoto[]
  userFriendList!: ShortUserInfo[]
  friend!: boolean
  requestToFriends!: boolean
  online!: boolean
  deleted!: boolean

}
