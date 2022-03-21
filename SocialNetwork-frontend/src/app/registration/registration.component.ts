import {Component, OnInit} from '@angular/core';
import {Router} from "@angular/router";
import {RegForm} from "../shared/models/reg-form";
import {AuthService} from "../shared/services/auth.service";
import {FormFields} from "../shared/constants/form-fields";
import {MessageDataService} from "../shared/services/message-data.service";
import {StatusCode} from "../shared/constants/status-code";
import {FormStatus} from "../shared/models/form-status";
import {CommonUtil} from "../shared/Utils/common-util";

@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.scss']
})
export class RegistrationComponent implements OnInit {

  readonly days = CommonUtil.DAYS
  readonly months = CommonUtil.MONTHS
  readonly years = CommonUtil.YEARS
  passwordVisibility = false
  regForm = new RegForm()
  errorMessages = new Map<string, string>();

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
      this.authService.registration(this.regForm).subscribe({
        next: data => {
          if (data.status == StatusCode.FAILURE) {
            this.errorMessages = CommonUtil.updateForm(form, data)
          } else if (data.status == StatusCode.SUCCESS) {
            this.messageDataService.title = "Привіт, " + this.regForm.firstName + "!"
            this.messageDataService.text = "Для завершення реєстрації, перейдіть за посиланням відправленим на вказану вами адресу електронної пошти."
            this.router.navigate(['/message'])
          } else {
            this.router.navigate([''])
          }
        },
        error: error => {
          this.router.navigate([''])
        }
      })
    } else {
      this.setFormFieldsTouched(form, true)
    }
  }

  setFormFieldsTouched(form: any, type: boolean) {
    form.controls.firstName.touched = type
    form.controls.lastName.touched = type
    form.controls.email.touched = type
    form.controls.username.touched = type
    form.controls.password.touched = type
    form.controls.dayOfBirth.touched = type
    form.controls.monthOfBirth.touched = type
    form.controls.yearOfBirth.touched = type
    form.controls.sex.touched = type
  }

  validateDateOfBirth(form: any) {
    if (form.value.dayOfBirth && form.value.monthOfBirth && form.value.yearOfBirth) {
      form.controls.dayOfBirth.touched = true
      form.controls.monthOfBirth.touched = true
      form.controls.yearOfBirth.touched = true
      if (!CommonUtil.checkDateOfBirth(CommonUtil.getDateString(form.value.monthOfBirth, form.value.dayOfBirth, form.value.yearOfBirth))) {
        form.controls.dayOfBirth.setErrors({"failed_validation": true})
        form.controls.monthOfBirth.setErrors({"failed_validation": true})
        form.controls.yearOfBirth.setErrors({"failed_validation": true})
      } else {
        form.controls.dayOfBirth.setErrors(null)
        form.controls.monthOfBirth.setErrors(null)
        form.controls.yearOfBirth.setErrors(null)
        this.errorMessages.delete('dateOfBirth')
      }
    }
  }

  togglePasswordVisibility() {
    this.passwordVisibility = !this.passwordVisibility;
  }

  onBackClick() {
    this.router.navigate([''])
  }

}
