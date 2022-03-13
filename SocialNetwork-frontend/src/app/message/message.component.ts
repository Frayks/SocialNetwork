import {Component, OnInit} from '@angular/core';
import {Router} from "@angular/router";
import {MessageDataService} from "../shared/services/message-data.service";

@Component({
  selector: 'app-message',
  templateUrl: './message.component.html',
  styleUrls: ['./message.component.scss']
})
export class MessageComponent implements OnInit {

  constructor(private router: Router, private messageDataService: MessageDataService) {
  }

  ngOnInit(): void {
    if (this.title == undefined || this.text == undefined) {
      this.router.navigate([''])
    }
  }

  get title():string {
    return this.messageDataService.title;
  }

  get text():string {
    return this.messageDataService.text;
  }

}
