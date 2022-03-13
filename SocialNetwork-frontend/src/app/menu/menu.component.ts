import {Component, Input, OnInit} from '@angular/core';
import {AuthService} from "../shared/services/auth.service";

@Component({
  selector: 'app-menu',
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.scss']
})
export class MenuComponent implements OnInit {

  @Input() numOfMessages!: number
  @Input() numOfRequestsToFriends!: number
  username!: string

  constructor(private authService: AuthService) {
    this.username = authService.getUsername()
  }

  ngOnInit(): void {
  }

}
