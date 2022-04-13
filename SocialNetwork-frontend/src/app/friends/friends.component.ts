import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute, Params, Router} from "@angular/router";
import {AuthService} from "../shared/services/auth.service";
import {UserFriendsInfo} from "../shared/models/user-friends-info";
import {FriendsService} from "../shared/services/friends.service";
import {ShortUserInfo} from "../shared/models/short-user-info";
import {MenuData} from "../shared/models/menu-data";
import {UserService} from "../shared/services/user.service";
import {WebSocketService} from "../shared/services/web-socket.service";
import {WebSocketMessage} from "../shared/models/web-socket-message";
import {WebSocketMessageType} from "../shared/constants/web-socket-message-type";
import {NotifyService} from "../shared/services/notify.service";

@Component({
  selector: 'app-friends',
  templateUrl: './friends.component.html',
  styleUrls: ['./friends.component.scss']
})
export class FriendsComponent implements OnInit, OnDestroy {

  displaySwitch = 1
  userFriendsInfo = new UserFriendsInfo()
  menuData = new MenuData()
  myProfile = false

  constructor(
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private authService: AuthService,
    private friendsService: FriendsService,
    private userService: UserService,
    private webSocketService: WebSocketService,
    private notifyService: NotifyService
  ) {
    this.userFriendsInfo.shortUserInfo = new ShortUserInfo();
  }

  async ngOnInit() {
    if (!this.authService.authCredentials) {
      await this.router.navigate(["/"])
      return
    }
    this.activatedRoute.params.subscribe((params: Params) => {
      let username = params['username']
      this.friendsService.loadUserFriendsInfo(username).subscribe({
        next: data => {
          this.userFriendsInfo = data
          this.myProfile = username === this.authService.getUsername()
        },
        error: () => {
          this.router.navigate(["/"])
        }
      })
      this.userService.loadMenuData().subscribe({
        next: data => {
          this.menuData = data
        },
        error: () => {

        }
      })
    })
    await this.webSocketService.initialize()
    this.webSocketService.webSocket.onmessage = (event) => {
      let webSocketMessage: WebSocketMessage = JSON.parse(event.data)
      switch (webSocketMessage.type) {
        case WebSocketMessageType.MESSAGE: {
          this.menuData.numOfMessages = this.menuData.numOfMessages + 1
          this.notifyService.notifySoundAndTitle(this.menuData.numOfMessages, NotifyService.SOUND_TYPE_1)
          break
        }
      }
    }
  }

  ngOnDestroy() {
    if (this.webSocketService.webSocket) {
      this.webSocketService.webSocket.onmessage = null
    }
  }

  deleteFriend(userId: number) {
    this.friendsService.deleteFriend(userId).subscribe({
      next: () => {
        this.userFriendsInfo.userFriendList = this.userFriendsInfo.userFriendList.filter(item => item.id !== userId)
      },
      error: () => {

      }
    })
  }

  agreeFriendRequest(userId: number) {
    this.friendsService.agreeFriendRequest(userId).subscribe({
      next: () => {
        let friends = this.userFriendsInfo.userFriendRequestList.filter(item => item.id == userId)
        this.userFriendsInfo.userFriendList = this.userFriendsInfo.userFriendList.concat(friends)
        this.userFriendsInfo.userFriendRequestList = this.userFriendsInfo.userFriendRequestList.filter(item => item.id !== userId)
        if (this.userFriendsInfo.userFriendRequestList.length == 0) {
          this.displaySwitch = 1
        }
      },
      error: () => {

      }
    })
  }

  rejectFriendRequest(userId: number) {
    this.friendsService.rejectFriendRequest(userId).subscribe({
      next: () => {
        this.userFriendsInfo.userFriendRequestList = this.userFriendsInfo.userFriendRequestList.filter(item => item.id !== userId)
        if (this.userFriendsInfo.userFriendRequestList.length == 0) {
          this.displaySwitch = 1
        }
      },
      error: () => {

      }
    })
  }

  switchToShowFriends() {
    this.displaySwitch = 1
  }

  switchToShowFriendRequests() {
    if (this.userFriendsInfo.userFriendRequestList.length > 0) {
      this.displaySwitch = 2
    }
  }

}
