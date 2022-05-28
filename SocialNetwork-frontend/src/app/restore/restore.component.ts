import {Component, OnInit} from '@angular/core';
import {AuthService} from "../shared/services/auth.service";
import {Router} from "@angular/router";
import {MessageDataService} from "../shared/services/message-data.service";
import {StatusCode} from "../shared/constants/status-code";
import CommonUtilCst from "../shared/utils/common-util-cst";

@Component({
  selector: 'app-restore',
  templateUrl: './restore.component.html',
  styleUrls: ['./restore.component.scss']
})
export class RestoreComponent implements OnInit {

  errorMessages = new Map<string, string>();

  constructor(
    private router: Router,
    private authService: AuthService,
    private messageDataService: MessageDataService
  ) {
  }

  async ngOnInit() {
    if (this.authService.authCredentials) {
      await this.router.navigate([`users/${this.authService.getUsername()}`])
      return
    }
  }

  onSubmit(form: any) {
    if (form.valid) {
      this.authService.restore(form.value.email).subscribe({
        next: data => {
          if (data.status == StatusCode.FAILURE) {
            this.errorMessages = CommonUtilCst.updateForm(form, data)
          } else if (data.status == StatusCode.SUCCESS) {
            this.messageDataService.title = "Зміна паролю"
            this.messageDataService.text = "Для зміни паролю, перейдіть за посиланням відправленим на вказану вами адресу електронної пошти та вкажіть новий пароль."
            this.router.navigate(['/message'])
          } else {
            this.router.navigate([''])
          }
        },
        error: () => {
          this.router.navigate([''])
        }
      })
    } else {
      this.setFormFieldsTouched(form, true)
    }
  }

  setFormFieldsTouched(form: any, type: boolean) {
    form.controls.email.touched = type
  }

}
