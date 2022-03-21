import {ShortUserInfo} from "./short-user-info";

export class Post {
  id!: number
  photoUri!: string
  text!: string
  numOfLikes!: number
  like!: boolean
  creationTime!: string
  shortUserInfo!: ShortUserInfo

}
