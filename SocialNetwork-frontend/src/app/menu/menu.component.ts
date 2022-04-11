import {Component, Input, OnInit} from '@angular/core';
import {AuthService} from "../shared/services/auth.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-menu',
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.scss']
})
export class MenuComponent implements OnInit {

  @Input() numOfMessages!: number
  @Input() numOfRequestsToFriends!: number
  username!: string

  constructor(
    private router: Router,
    private authService: AuthService
  ) {
  }

  async ngOnInit() {
    if (!this.authService.authCredentials) {
      await this.router.navigate(["/"])
      return
    }
    this.username = this.authService.getUsername()
  }

}
