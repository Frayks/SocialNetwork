import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Params, Router} from "@angular/router";
import {AuthService} from "../shared/services/auth.service";
import {MessageDataService} from "../shared/services/message-data.service";

@Component({
  selector: 'app-confirm',
  templateUrl: './confirm.component.html',
  styleUrls: ['./confirm.component.scss']
})
export class ConfirmComponent implements OnInit {

  constructor(
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private authService: AuthService,
    private messageDataService: MessageDataService
  ) {
  }

  ngOnInit(): void {
    if (this.authService.authCredentials) {
      this.router.navigate([`users/${this.authService.getUsername()}`])
    }
    this.activatedRoute.params.subscribe((params: Params) => {
      this.authService.confirm(params['key']).subscribe({
        next: data => {
          this.messageDataService.title = "Вітаю!"
          this.messageDataService.text = "Акаунт успішно створено."
          this.router.navigate(['/message'])
        },
        error: error => {
          this.router.navigate([''])
        }
      })
    })
  }

}
