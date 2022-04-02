import {Component, ElementRef, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {UserService} from "../shared/services/user.service";
import {MenuData} from "../shared/models/menu-data";
import {SettingsService} from "../shared/services/settings.service";
import {AuthService} from "../shared/services/auth.service";
import {Router} from "@angular/router";
import {BasicSettings} from "../shared/models/basic-settings";
import {AdditionalSettings} from "../shared/models/additional-settings";
import {StatusCode} from "../shared/constants/status-code";
import {CommonUtil} from "../shared/Utils/common-util";
import {WebSocketMessage} from "../shared/models/web-socket-message";
import {WebSocketMessageType} from "../shared/constants/web-socket-message-type";
import {WebSocketService} from "../shared/services/web-socket.service";

@Component({
  selector: 'app-settings',
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.scss']
})
export class SettingsComponent implements OnInit, OnDestroy {

  readonly days = CommonUtil.DAYS
  readonly months = CommonUtil.MONTHS
  readonly years = CommonUtil.YEARS

  menuData = new MenuData()
  @ViewChild('photoFile')
  private photoFileElement!: ElementRef
  selectedPhoto: any
  selectedPhotoValid = true
  photoURL: any
  basicSettings = new BasicSettings()
  additionalSettings = new AdditionalSettings()
  errorMessages = new Map<string, string>();
  displaySwitch = 1
  basicSettingsFormStatus = 1;
  additionalSettingsFormStatus = 1;

  constructor(
    private router: Router,
    private authService: AuthService,
    private userService: UserService,
    private settingsService: SettingsService,
    private webSocketService: WebSocketService
  ) {
  }

  async ngOnInit() {
    if (!this.authService.authCredentials) {
      await this.router.navigate(["/"])
    }
    this.settingsService.loadSettings().subscribe({
      next: data => {
        this.basicSettings = data.basicSettings
        this.additionalSettings = data.additionalSettings
      },
      error: () => {

      }
    })
    this.userService.loadMenuData().subscribe({
        next: data => {
          this.menuData = data
        },
        error: () => {

        }
      }
    )
    await this.webSocketService.initialize()
    this.webSocketService.webSocket.onmessage = (event) => {
      let webSocketMessage: WebSocketMessage = JSON.parse(event.data)
      switch (webSocketMessage.type) {
        case WebSocketMessageType.MESSAGE: {
          this.menuData.numOfMessages = this.menuData.numOfMessages + 1
          break
        }
      }
    }
  }

  ngOnDestroy() {
    if (this.webSocketService.webSocket) {
      this.webSocketService.webSocket.onmessage = null
    }
  }

  onSubmitBasicSettings(form: any) {
    if (this.basicSettingsFormStatus == 0) {
      if (this.selectedPhotoValid && form.valid) {
        let payload = new FormData()
        if (this.selectedPhoto) {
          payload.append("photo", this.selectedPhoto, this.selectedPhoto.name)
        }
        payload.append("basicSettings", JSON.stringify(this.basicSettings))
        this.settingsService.changeBasicSettings(payload).subscribe({
          next: data => {
            if (data.status == StatusCode.FAILURE) {
              this.errorMessages = CommonUtil.updateForm(form, data)
              if (this.errorMessages.has('fileInput')) {
                this.selectedPhotoValid = false
              }
            } else if (data.status == StatusCode.SUCCESS) {
              this.basicSettingsFormStatus = 1;
            } else {
              this.router.navigate([''])
            }
          },
          error: () => {

          }
        })
      } else {
        this.setBasicSettingsFormFieldsTouched(form, true)
      }
    }
  }

  onSubmitAdditionalSettings(form: any) {
    if (this.additionalSettingsFormStatus == 0) {
      if (form.valid) {
        this.settingsService.changeAdditionalSettings(this.additionalSettings).subscribe({
          next: data => {
            if (data.status == StatusCode.FAILURE) {
              this.errorMessages = CommonUtil.updateForm(form, data)
            } else if (data.status == StatusCode.SUCCESS) {
              this.additionalSettingsFormStatus = 1;
            } else {
              this.router.navigate([''])
            }
          },
          error: () => {

          }
        })
      } else {
        this.setAdditionalSettingsFormFieldsTouched(form, true)
      }
    }
  }

  setAdditionalSettingsFormFieldsTouched(form: any, type: boolean) {
    form.controls.aboutYourself.touched = type
    form.controls.city.touched = type
    form.controls.school.touched = type
    form.controls.university.touched = type
  }

  setBasicSettingsFormFieldsTouched(form: any, type: boolean) {
    form.controls.fileInput.touched = type
    form.controls.firstName.touched = type
    form.controls.lastName.touched = type
    form.controls.username.touched = type
    form.controls.dayOfBirth.touched = type
    form.controls.monthOfBirth.touched = type
    form.controls.yearOfBirth.touched = type
    form.controls.sex.touched = type
  }

  validateDateOfBirth(form: any) {
    CommonUtil.validateDateOfBirth(form, this.errorMessages)
    this.basicSettingsChanged()
  }

  photoSelected(event: any) {
    this.selectedPhoto = event.target.files[0]
    this.selectedPhotoValid = CommonUtil.mapToMb(this.selectedPhoto.size) < 2
    const reader = new FileReader()
    reader.readAsDataURL(this.selectedPhoto)
    reader.onload = () => {
      this.photoURL = reader.result
    }
    this.basicSettingsChanged()
  }

  clearSelectedPhoto() {
    this.photoFileElement.nativeElement.value = ""
    this.selectedPhoto = null
    this.photoURL = null
    this.basicSettingsFormStatus = 1;
  }

  additionalSettingsChanged() {
    this.additionalSettingsFormStatus = 0;
  }

  basicSettingsChanged() {
    this.basicSettingsFormStatus = 0;
  }

  switchToBasicSettings() {
    this.displaySwitch = 1
  }

  switchToAdditionalSettings() {
    this.displaySwitch = 2
  }

}
