import {Component, OnInit} from '@angular/core';
import {AuthService} from "../shared/services/auth.service";
import {ActivatedRoute, Params, Router} from "@angular/router";
import {UserService} from "../shared/services/user.service";
import {map} from "rxjs";
import {UserProfileInfo} from "../shared/models/UserProfileInfo";

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.scss']
})
export class UserComponent implements OnInit {

  userProfileInfo = new UserProfileInfo()

  showAdditionalInfo = false;
  showPosts = true;
  myProfile = true;


  constructor(private router: Router,private activatedRoute: ActivatedRoute, private authService: AuthService, private userService: UserService) {
  }

  ngOnInit(): void {
    if (!this.authService.authCredentials) {
      this.router.navigate(["/"])
    }
    this.activatedRoute.params.subscribe((params: Params) => {
      this.userService.loadUserProfileInfo(params['username']).subscribe({
        next: data => {
          this.userProfileInfo = data
        },
        error: error => {
          this.router.navigate([`users/${this.authService.getUsername()}`])
        }
      })
    })
  }

  toggleShowAdditionalInfo() {
    this.showAdditionalInfo = !this.showAdditionalInfo;
  }

  toggleShowPosts() {
    this.showPosts = !this.showPosts;
  }

  calcAge() {
    let date = Date.parse(this.userProfileInfo.dateOfBirth)
    let timeDiff = Math.abs(Date.now() - new Date(date).getTime())
    return Math.floor(timeDiff / (1000 * 3600 * 24) / 365.25)
  }


}
