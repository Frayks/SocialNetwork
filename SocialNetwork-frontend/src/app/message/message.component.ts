import {Component, OnInit} from '@angular/core';
import {Router} from "@angular/router";
import {MessageDataService} from "../shared/services/message-data.service";
import {AuthService} from "../shared/services/auth.service";

@Component({
  selector: 'app-message',
  templateUrl: './message.component.html',
  styleUrls: ['./message.component.scss']
})
export class MessageComponent implements OnInit {

  constructor(
    private router: Router,
    private messageDataService: MessageDataService,
    private authService: AuthService
  ) {
  }

  async ngOnInit() {
    if (this.authService.authCredentials) {
      await this.router.navigate([`users/${this.authService.getUsername()}`])
      return
    }
    if (this.title == undefined || this.text == undefined) {
      await this.router.navigate([''])
      return
    }
  }

  get title(): string {
    return this.messageDataService.title;
  }

  get text(): string {
    return this.messageDataService.text;
  }

}
