import {Component, OnInit} from '@angular/core';
import {Router} from "@angular/router";

@Component({
  selector: 'app-restore-message',
  templateUrl: './restore-message.component.html',
  styleUrls: ['./restore-message.component.scss']
})
export class RestoreMessageComponent implements OnInit {

  email = "email@email.com"
  private router: Router

  constructor(router: Router) {
    this.router = router
  }

  ngOnInit(): void {
  }

  onCloseClick() {
    this.router.navigate([''])
  }

}
