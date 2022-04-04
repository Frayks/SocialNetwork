import {Component, OnDestroy, OnInit} from '@angular/core';
import {WebSocketService} from "../shared/services/web-socket.service";
import {MessengerService} from "../shared/services/messenger.service";
import {UserChatInfo} from "../shared/models/user-chat-info";
import {WebSocketMessage} from "../shared/models/web-socket-message";
import {MenuData} from "../shared/models/menu-data";
import {AuthService} from "../shared/services/auth.service";
import {Router} from "@angular/router";
import {UserService} from "../shared/services/user.service";
import {WebSocketMessageType} from "../shared/constants/web-socket-message-type";
import {Message} from "../shared/models/message";
import {ViewedMessagesData} from "../shared/models/viewed-messages-data";
import {ChatMessage} from "../shared/models/chat-message";
import {ChatInfoData} from "../shared/models/chat-info-data";
import CommonUtilCst from "../shared/utils/common-util-cst";
import {environment} from "../../environments/environment";

@Component({
  selector: 'app-messenger',
  templateUrl: './messenger.component.html',
  styleUrls: ['./messenger.component.scss']
})
export class MessengerComponent implements OnInit, OnDestroy {

  selectedChat!: UserChatInfo
  chatInfoData!: ChatInfoData
  menuData = new MenuData()

  constructor(
    private router: Router,
    private authService: AuthService,
    private userService: UserService,
    private webSocketService: WebSocketService,
    private messengerService: MessengerService
  ) {
  }

  async ngOnInit() {
    if (!this.authService.authCredentials) {
      await this.router.navigate(["/"])
    }
    let urlTree = this.router.parseUrl(this.router.url);
    let chatWith = urlTree.queryParams["chatWith"]
    this.userService.loadMenuData().subscribe({
        next: data => {
          this.menuData = data
        },
        error: () => {
        }
      }
    )

    this.messengerService.loadChatInfoData(chatWith).subscribe({
      next: data => {
        this.chatInfoData = data
        if (this.chatInfoData.userChatInfoList.length > 0) {
          this.sortUserChatInfoListByNumOfUnreadMessages()
          if (chatWith) {
            let userChatInfoList: UserChatInfo[] = this.chatInfoData.userChatInfoList.filter(x => x.username == chatWith)
            if (userChatInfoList.length > 0) {
              this.selectChat(userChatInfoList[0])
            } else {
              this.selectChat(this.chatInfoData.userChatInfoList[0])
            }
          } else {
            this.selectChat(this.chatInfoData.userChatInfoList[0])
          }
        }
      }
    })

    await this.webSocketService.initialize()
    this.webSocketService.webSocket.onmessage = (event) => {
      let webSocketMessage: WebSocketMessage = JSON.parse(event.data)
      this.handleWebSocketMessage(webSocketMessage)
    }

  }

  ngOnDestroy() {
    if (this.webSocketService.webSocket) {
      this.webSocketService.webSocket.onmessage = null
    }
  }

  async selectChat(userChatInfo: UserChatInfo) {
    this.selectedChat = userChatInfo
    if (!this.selectedChat.chatMessageList) {
      let data = <ChatMessage[]>await this.messengerService.loadChatMessageList(this.selectedChat.id).toPromise()
      this.selectedChat.showLoadOldMessagesBtn = data.length > 0
      this.selectedChat.chatMessageList = data
    }
    this.setChatOnFirstPosition(userChatInfo)
    let newMessagesList = this.selectedChat.chatMessageList.filter(x => !x.revised && x.userId != this.chatInfoData.userId)
    if (newMessagesList.length > 0) {
      let viewedMessagesIdsList: Number[] = []
      newMessagesList.forEach(newMessage => {
        newMessage.newAndNotRevised = true
        newMessage.revised = true
        viewedMessagesIdsList.push(newMessage.id)
      })
      this.sendViewedMessagesIdsList(this.selectedChat.id, viewedMessagesIdsList)
      this.menuData.numOfMessages = this.menuData.numOfMessages - viewedMessagesIdsList.length
    }
    this.selectedChat.numOfUnreadMessages = 0
  }

  sortUserChatInfoListByNumOfUnreadMessages() {
    this.chatInfoData.userChatInfoList.sort(
      function (x, y) {
        return x.numOfUnreadMessages > y.numOfUnreadMessages ? -1 : 1
      }
    )
  }

  setChatOnFirstPosition(userChatInfo: UserChatInfo) {
    this.chatInfoData.userChatInfoList.sort(
      function (x, y) {
        return x == userChatInfo ? -1 : y == userChatInfo ? 1 : 0;
      }
    )
  }

  loadChatMessageListBeforeTime() {
    let beforeTime = this.selectedChat.chatMessageList[0].creationTime
    this.messengerService.loadChatMessageListBeforeTime(this.selectedChat.id, beforeTime).subscribe({
      next: data => {
        this.selectedChat.chatMessageList = data.concat(this.selectedChat.chatMessageList)
        this.selectedChat.showLoadOldMessagesBtn = data.length > 0
      },
      error: () => {
      }
    })
  }

  handleWebSocketMessage(webSocketMessage: WebSocketMessage) {
    switch (webSocketMessage.type) {
      case WebSocketMessageType.MESSAGE: {
        let chatMessage: ChatMessage = webSocketMessage.body
        let targetChat: UserChatInfo = this.chatInfoData.userChatInfoList.filter(x => x.id == chatMessage.chatId)[0]
        if (targetChat.chatMessageList) {
          targetChat.chatMessageList.push(chatMessage)
        }
        if (targetChat == this.selectedChat) {
          if (!chatMessage.revised && chatMessage.userId != this.chatInfoData.userId) {
            this.sendViewedMessagesIdsList(targetChat.id, [chatMessage.id])
          }
        } else {
          targetChat.numOfUnreadMessages = targetChat.numOfUnreadMessages ? targetChat.numOfUnreadMessages + 1 : 1
          this.menuData.numOfMessages = this.menuData.numOfMessages + 1
          this.sortUserChatInfoListByNumOfUnreadMessages()
          this.setChatOnFirstPosition(this.selectedChat)
        }
        break
      }
      case WebSocketMessageType.VIEWED_MESSAGES: {
        let viewedMessagesData: ViewedMessagesData = webSocketMessage.body
        let viewedMessagesIdsList: Number[] = viewedMessagesData.viewedMessagesIdsList
        let targetChat: UserChatInfo = this.chatInfoData.userChatInfoList.filter(x => x.id == viewedMessagesData.chatId)[0]
        if (targetChat.chatMessageList) {
          let chatMessageMap = new Map<Number, ChatMessage>(targetChat.chatMessageList.map(x => [x.id, x]))
          viewedMessagesIdsList.forEach(viewedMessageId => {
            let chatMessage = chatMessageMap.get(viewedMessageId)
            if (chatMessage) {
              chatMessage.revised = true
            }
          })
        }
        break
      }
      case WebSocketMessageType.NEW_CHAT: {
        let userChatInfo: UserChatInfo = webSocketMessage.body
        this.chatInfoData.userChatInfoList.push(userChatInfo)
        if (!this.selectedChat) {
          this.selectedChat = userChatInfo
          this.selectedChat.chatMessageList = []
        }
      }
    }
  }

  sendMessage(chatId: number, text: string) {
    let webSocketMessage = new WebSocketMessage(WebSocketMessageType.MESSAGE)
    let message = new Message()
    message.chatId = chatId
    message.text = text
    webSocketMessage.body = message
    this.selectedChat.textInput = ""
    this.webSocketService.sendMessage(webSocketMessage)
  }

  sendViewedMessagesIdsList(chatId: number, viewedMessagesIdsList: Number[]) {
    let webSocketMessage = new WebSocketMessage(WebSocketMessageType.VIEWED_MESSAGES)
    let viewedMessagesData = new ViewedMessagesData()
    viewedMessagesData.chatId = chatId
    viewedMessagesData.viewedMessagesIdsList = viewedMessagesIdsList;
    webSocketMessage.body = viewedMessagesData
    this.webSocketService.sendMessage(webSocketMessage)
  }


  getDateFormat(creationTime: string) {
    return CommonUtilCst.getDateFormat(creationTime)
  }

  getMaxChatMessageTextLength() {
    return environment.maxChatMessageTextLength
  }

}
