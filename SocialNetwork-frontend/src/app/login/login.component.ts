import {Component, OnInit} from '@angular/core';
import {Router} from "@angular/router";
import {AuthService} from "../shared/services/auth.service";
import {NgForm} from "@angular/forms";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  errorMessage!: string

  constructor(
    private router: Router,
    private authService: AuthService
  ) {
  }

  ngOnInit(): void {
    if(this.authService.authCredentials) {
      this.router.navigate([`users/${this.authService.getUsername()}`])
    }
  }

  onRegClick() {
    this.router.navigate(['/registration'])
  }

  onSubmit(form: NgForm) {
    if (form.valid) {
      this.authService.login(form.value.username, form.value.password).subscribe({
          next: () => {
            this.router.navigate([`users/${this.authService.getUsername()}`])
          },
          error: error => {
            switch (error.status) {
              case 403: {
                this.errorMessage = "Неправильний логін або пароль!"
                break
              }
              default: {
                this.errorMessage = "Непередбачувана помилка!"
              }
            }
          }
        })
    } else {

    }
  }

}
