import {ChatMessage} from "./chat-message";

export class UserChatInfo {
  id!: number
  userId!: number
  username!: string
  firstName!: string
  lastName!: string
  avatarUri!: string
  chatMessageList!: ChatMessage[]
  textInput!: string
  numOfUnreadMessages!: number
  showLoadOldMessagesBtn!: boolean
  online!: boolean

}
