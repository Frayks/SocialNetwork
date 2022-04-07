import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {MatDialogRef} from "@angular/material/dialog";
import {NgForm} from "@angular/forms";
import CommonUtilCst from "../shared/utils/common-util-cst";
import {environment} from "../../environments/environment";
import {UserService} from "../shared/services/user.service";
import {StatusCode} from "../shared/constants/status-code";
import {Router} from "@angular/router";

@Component({
  selector: 'app-create-post-dialog',
  templateUrl: './create-post-dialog.component.html',
  styleUrls: ['./create-post-dialog.component.scss']
})
export class CreatePostDialogComponent implements OnInit {

  @ViewChild('photoFile')
  private photoFileElement!: ElementRef
  selectedPhoto: any
  selectedPhotoValid = false
  photoURL: any
  errorMessages = new Map<string, string>();

  constructor(
    private dialogRef: MatDialogRef<CreatePostDialogComponent>,
    private userService: UserService,
    private router: Router
  ) {
  }

  ngOnInit(): void {
  }

  photoSelected(event: any) {
    if (event.target.files.length == 1) {
      this.selectedPhoto = event.target.files[0]
      this.selectedPhotoValid = this.selectedPhoto && CommonUtilCst.mapToMb(this.selectedPhoto.size) < environment.maxImageSize
      const reader = new FileReader()
      reader.readAsDataURL(this.selectedPhoto)
      reader.onload = () => {
        this.photoURL = reader.result
      }
    }
  }

  clearSelectedPhoto() {
    this.photoFileElement.nativeElement.value = ""
    this.selectedPhoto = null
    this.photoURL = null
  }

  onSubmit(form: NgForm) {
    if (this.selectedPhoto || form.value.postText) {
      if (this.selectedPhotoValid && form.valid) {
        let payload = new FormData()
        if (this.selectedPhotoValid) {
          payload.append("postPhoto", this.selectedPhoto, this.selectedPhoto.name)
        }
        if (form.value.postText) {
          payload.append("postText", form.value.postText)
        }
        this.userService.createPost(payload).subscribe({
          next: data => {
            if (data.status == StatusCode.FAILURE) {
              this.errorMessages = CommonUtilCst.updateForm(form, data)
            } else if (data.status == StatusCode.SUCCESS) {
              this.dialogRef.close(true)
            } else {
              this.router.navigate([''])
            }
          },
          error: () => {
          }
        })
      } else {
        if (!this.selectedPhotoValid) {
          this.errorMessages.set('fileInput', "Розмір зображення надто великий. Максимальний розмір зображення: " + environment.maxImageSize + "MB")
        }
        this.setFormFieldsTouched(form, true)
      }
    } else {
      this.errorMessages.set('allFields', "Необхідно заповнити хоча б одне поле форми!")
    }
  }

  cancelCreate() {
    this.dialogRef.close()
  }

  getMaxPostTextLength() {
    return environment.maxPostTextLength
  }

  setFormFieldsTouched(form: any, type: boolean) {
    form.controls.fileInput.touched = type
    form.controls.postText.touched = type
  }
}
