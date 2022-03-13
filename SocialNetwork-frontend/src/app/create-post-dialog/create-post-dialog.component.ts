import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {MatDialogRef} from "@angular/material/dialog";
import {NgForm} from "@angular/forms";

@Component({
  selector: 'app-create-post-dialog',
  templateUrl: './create-post-dialog.component.html',
  styleUrls: ['./create-post-dialog.component.scss']
})
export class CreatePostDialogComponent implements OnInit {

  @ViewChild('photoFile')
  private photoFileElement!: ElementRef
  selectedPhoto: any
  photoURL: any

  constructor(private dialogRef: MatDialogRef<CreatePostDialogComponent>) {
  }

  ngOnInit(): void {
  }

  photoSelected(event: any) {
    this.selectedPhoto = event.target.files[0]
    const reader = new FileReader()
    reader.readAsDataURL(this.selectedPhoto)
    reader.onload = () => {
      this.photoURL = reader.result
    }
  }

  clearSelectedPhoto() {
    this.photoFileElement.nativeElement.value = ""
    this.selectedPhoto = null
    this.photoURL = null
  }

  onSubmit(form: NgForm) {
    let photoValid = this.selectedPhoto && this.mapToMb(this.selectedPhoto.size) < 10
    if (photoValid || form.value.text) {
      let payload = new FormData()
      if (photoValid) {
        payload.append("photo", this.selectedPhoto, this.selectedPhoto.name)
      }
      if (form.value.text) {
        payload.append("text", form.value.text)
      }
      this.dialogRef.close(payload)
    }
  }

  cancelCreate() {
    this.dialogRef.close()
  }

  mapToMb(size: number): number {
    return size / 1024 / 1024
  }

}
