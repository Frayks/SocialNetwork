import {Component, OnInit} from '@angular/core';
import {AuthService} from "../shared/services/auth.service";
import {Router} from "@angular/router";
import {MessageDataService} from "../shared/services/message-data.service";

@Component({
  selector: 'app-restore',
  templateUrl: './restore.component.html',
  styleUrls: ['./restore.component.scss']
})
export class RestoreComponent implements OnInit {

  constructor(
    private router: Router,
    private authService: AuthService,
    private messageDataService: MessageDataService
  ) {
  }

  ngOnInit(): void {
    if (this.authService.authCredentials) {
      this.router.navigate([`users/${this.authService.getUsername()}`])
    }
  }

  onSubmit(form: any) {
    if (form.valid) {
      this.authService.restore(form.value.email).subscribe({
        next: () => {
          this.messageDataService.title = "Зміна паролю"
          this.messageDataService.text = "Для зміни паролю, перейдіть за посиланням відправленим на вказану вами адресу електронної пошти та вкажіть новий пароль"
          this.router.navigate(['/message'])
        },
        error: () => {

        }
      })
    }
  }
}
