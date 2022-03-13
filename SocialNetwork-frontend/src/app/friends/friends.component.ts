import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Params, Router} from "@angular/router";
import {AuthService} from "../shared/services/auth.service";
import {UserFriendsInfo} from "../shared/models/UserFriendsInfo";
import {FriendsService} from "../shared/services/friends.service";
import {ShortUserInfo} from "../shared/models/ShortUserInfo";
import {MenuData} from "../shared/models/MenuData";
import {UserService} from "../shared/services/user.service";

@Component({
  selector: 'app-friends',
  templateUrl: './friends.component.html',
  styleUrls: ['./friends.component.scss']
})
export class FriendsComponent implements OnInit {

  displaySwitch = 1
  userFriendsInfo = new UserFriendsInfo()
  menuData = new MenuData()
  myProfile = false

  constructor(private router: Router,
              private activatedRoute: ActivatedRoute,
              private authService: AuthService,
              private friendsService: FriendsService,
              private userService: UserService
  ) {
    this.userFriendsInfo = new UserFriendsInfo()
    this.userFriendsInfo.shortUserInfo = new ShortUserInfo();
  }

  ngOnInit(): void {
    if (!this.authService.authCredentials) {
      this.router.navigate(["/"])
    }
    this.activatedRoute.params.subscribe((params: Params) => {
      let username = params['username']
      this.friendsService.loadUserFriendsInfo(username).subscribe({
        next: data => {
          this.userFriendsInfo = data
          this.myProfile = username === this.authService.getUsername()
        },
        error: error => {
          this.router.navigate(["/"])
        }
      })
      this.userService.loadMenuData().subscribe({
        next:data => {
          this.menuData = data
        },
        error: () => {

        }
      })
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
      next: data => {
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
}
