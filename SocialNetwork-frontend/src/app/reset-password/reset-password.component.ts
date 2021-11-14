import {Component, OnInit} from '@angular/core';

@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.scss']
})
export class ResetPasswordComponent implements OnInit {

  passwordVisibility = false
  repeatPasswordVisibility = false

  constructor() {
  }

  ngOnInit(): void {
  }

  togglePasswordVisibility() {
    this.passwordVisibility = !this.passwordVisibility;
  }

  toggleRepeatPasswordVisibility() {
    this.repeatPasswordVisibility = !this.repeatPasswordVisibility;
  }

}
