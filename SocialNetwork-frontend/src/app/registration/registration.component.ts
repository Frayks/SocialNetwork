import {Component, OnInit} from '@angular/core';
import {Router} from "@angular/router";
import {RegForm} from "../shared/models/RegForm";
import {AuthService} from "../shared/services/auth.service";
import {RegStatusCode} from "../shared/constants/reg-status-code";
import {RegStatus} from "../shared/models/RegStatus";
import {RegFormField} from "../shared/constants/reg-form-field";
import {MessageDataService} from "../shared/services/message-data.service";

@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.scss']
})
export class RegistrationComponent implements OnInit {

  passwordVisibility = false
  readonly months = ['Січня', 'Лютого', 'Березня', 'Квітня', 'Травня', 'Червня', 'Липня', 'Серпня', 'Вересня', 'Жовтня', 'Листопада', 'Грудня']
  readonly days = Array.from({length: 31}, (_, i) => i + 1)
  readonly years = Array.from({length: 101}, (_, i) => i + new Date().getFullYear() - 100).reverse()
  regForm = new RegForm()
  errorMessages = new Map<string, string>();

  constructor(private router: Router,
              private authService: AuthService,
              private messageDataService: MessageDataService) {
  }

  ngOnInit(): void {

  }

  onSubmit(form: any) {
    if (form.valid) {
      this.authService.registration(this.regForm).subscribe({
        next: data => {
          if (data.status == RegStatusCode.FAILURE) {
            this.updateRegForm(form, data)
          } else if (data.status == RegStatusCode.SUCCESS) {
            this.messageDataService.title = 'Привіт, ' + this.regForm.firstName + "!"
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

  updateRegForm(form: any, regStatus: RegStatus) {
    if (regStatus.invalidFieldsMap) {
      this.errorMessages.clear()
      if (regStatus.invalidFieldsMap[RegFormField.FIRST_NAME]) {
        form.controls.firstName.setErrors({"failed_validation": true})
        this.errorMessages.set('firstName',regStatus.invalidFieldsMap[RegFormField.FIRST_NAME])
      }
      if (regStatus.invalidFieldsMap[RegFormField.LAST_NAME]) {
        form.controls.lastName.setErrors({"failed_validation": true})
        this.errorMessages.set('lastName',regStatus.invalidFieldsMap[RegFormField.LAST_NAME])
      }
      if (regStatus.invalidFieldsMap[RegFormField.EMAIL]) {
        form.controls.email.setErrors({"failed_validation": true})
        this.errorMessages.set('email',regStatus.invalidFieldsMap[RegFormField.EMAIL])
      }
      if (regStatus.invalidFieldsMap[RegFormField.USERNAME]) {
        form.controls.username.setErrors({"failed_validation": true})
        this.errorMessages.set('username',regStatus.invalidFieldsMap[RegFormField.USERNAME])
      }
      if (regStatus.invalidFieldsMap[RegFormField.PASSWORD]) {
        form.controls.password.setErrors({"failed_validation": true})
        this.errorMessages.set('password',regStatus.invalidFieldsMap[RegFormField.PASSWORD])
      }
      if (regStatus.invalidFieldsMap[RegFormField.DATE_OF_BIRTH]) {
        form.controls.dayOfBirth.setErrors({"failed_validation": true})
        form.controls.monthOfBirth.setErrors({"failed_validation": true})
        form.controls.yearOfBirth.setErrors({"failed_validation": true})
        this.errorMessages.set('dateOfBirth',regStatus.invalidFieldsMap[RegFormField.DATE_OF_BIRTH])
      }
      if (regStatus.invalidFieldsMap[RegFormField.SEX]) {
        form.controls.sex.setErrors({"failed_validation": true})
        this.errorMessages.set('sex',regStatus.invalidFieldsMap[RegFormField.SEX])
      }
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
      if (!this.checkDateOfBirth(this.getDateString(form.value.monthOfBirth, form.value.dayOfBirth, form.value.yearOfBirth))) {
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

  checkDateOfBirth(dateOfBirth: string) {
    let date = new Date(dateOfBirth)
    let dateNow = new Date()
    return !isNaN(date.getDate()) && date.getTime() < dateNow.getTime() && dateNow.getFullYear() - date.getFullYear() <= 100
  }

  getDateString(monthOfBirth: string, dayOfBirth: string, yearOfBirth: string) {
    return monthOfBirth + "." + dayOfBirth + "." + yearOfBirth
  }

  togglePasswordVisibility() {
    this.passwordVisibility = !this.passwordVisibility;
  }

  onBackClick() {
    this.router.navigate([''])
  }

}
