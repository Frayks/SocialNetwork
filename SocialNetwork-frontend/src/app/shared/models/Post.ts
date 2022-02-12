import {ShortUserInfo} from "./ShortUserInfo";

export class Post {
  id!: number
  photoUrl!: string
  text!: string
  numOfLikes!: number
  creationTime!: string
  shortUserInfo!: ShortUserInfo

}
