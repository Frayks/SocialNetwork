import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Params, Router} from "@angular/router";
import {AuthService} from "../shared/services/auth.service";
import {UserService} from "../shared/services/user.service";
import {UserFriendsInfo} from "../shared/models/UserFriendsInfo";
import {FriendsService} from "../shared/services/friends.service";

@Component({
  selector: 'app-friends',
  templateUrl: './friends.component.html',
  styleUrls: ['./friends.component.scss']
})
export class FriendsComponent implements OnInit {

  displaySwitch = 1
  userFriendsInfo = new UserFriendsInfo()
  myProfile = false

  constructor(private router: Router, private activatedRoute: ActivatedRoute, private authService: AuthService, private friendsService: FriendsService) {
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
          console.dir(this.userFriendsInfo)
        },
        error: error => {
          this.router.navigate(["/"])
        }
      })
    })
  }

  switchToShowFriends() {
    this.displaySwitch = 1
  }

  switchToShowFriendRequests() {
    this.displaySwitch = 2
  }

  deleteFriend(id: number) {

  }

  agreeRequest(id: number) {

  }

  rejectRequest(id: number) {

  }
}
