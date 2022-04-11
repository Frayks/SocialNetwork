import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Params, Router} from "@angular/router";
import {MessageDataService} from "../shared/services/message-data.service";
import {AuthService} from "../shared/services/auth.service";
import {ResetPasswordRequest} from "../shared/models/reset-password-request";
import {StatusCode} from "../shared/constants/status-code";
import CommonUtilCst from "../shared/utils/common-util-cst";

@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.scss']
})
export class ResetPasswordComponent implements OnInit {

  passwordVisibility = false
  repeatPasswordVisibility = false
  resetPasswordRequest = new ResetPasswordRequest()
  errorMessages = new Map<string, string>();

  constructor(
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private authService: AuthService,
    private messageDataService: MessageDataService
  ) {
  }

  async ngOnInit() {
    if (this.authService.authCredentials) {
      await this.router.navigate([`users/${this.authService.getUsername()}`])
      return
    }
    this.activatedRoute.params.subscribe((params: Params) => {
      this.resetPasswordRequest.restoreKey = params['key']
    })
  }

  onSubmit(form: any) {
    if (form.valid) {
      this.authService.resetPassword(this.resetPasswordRequest).subscribe({
        next: data => {
          if (data.status == StatusCode.FAILURE) {
            this.errorMessages = CommonUtilCst.updateForm(form, data)
            if (this.errorMessages.has('password')) {
              this.resetPasswordRequest.newPassword = ''
              form.value.repeatPassword = ''
              form.controls.repeatPassword.touched = false
            }
          } else if (data.status == StatusCode.SUCCESS) {
            this.messageDataService.title = "Вітаю!"
            this.messageDataService.text = "Пароль успішно змінено."
            this.router.navigate(['/message'])
          } else {
            this.router.navigate([''])
          }
        },
        error: () => {

        }
      })
    } else {
      this.setFormFieldsTouched(form, true)
    }
  }

  validateRepeatPassword(form: any) {
    if (!form.controls.password.valid || form.value.password != form.value.repeatPassword) {
      form.controls.repeatPassword.setErrors({"failed_validation": true})
    } else {
      form.controls.repeatPassword.setErrors(null)
    }
  }

  setFormFieldsTouched(form: any, type: boolean) {
    form.controls.password.touched = type
    form.controls.repeatPassword.touched = type
  }

  togglePasswordVisibility() {
    this.passwordVisibility = !this.passwordVisibility;
  }

  toggleRepeatPasswordVisibility() {
    this.repeatPasswordVisibility = !this.repeatPasswordVisibility;
  }

}
