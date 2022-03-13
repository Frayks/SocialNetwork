import {ShortUserInfo} from "./ShortUserInfo";

export class Post {
  id!: number
  photoUri!: string
  text!: string
  numOfLikes!: number
  creationTime!: string
  shortUserInfo!: ShortUserInfo

}
