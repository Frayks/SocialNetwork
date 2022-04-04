import {Component, Inject, OnInit} from '@angular/core';
import {UserPhoto} from "../shared/models/user-photo";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {UserService} from "../shared/services/user.service";

@Component({
  selector: 'app-view-photo-dialog',
  templateUrl: './view-photo-dialog.component.html',
  styleUrls: ['./view-photo-dialog.component.scss']
})
export class ViewPhotoDialogComponent implements OnInit {

  photo: UserPhoto

  constructor(
    private dialogRef: MatDialogRef<ViewPhotoDialogComponent>,
    @Inject(MAT_DIALOG_DATA) photo: UserPhoto,
    private userService: UserService
  ) {
    this.photo = photo
  }

  ngOnInit(): void {
  }

  changePhotoLike() {
    this.userService.changePhotoLike(this.photo.id).subscribe({
      next: () => {
        if (this.photo.like) {
          this.photo.like = false;
          this.photo.numOfLikes = this.photo.numOfLikes - 1;
        } else {
          this.photo.like = true;
          this.photo.numOfLikes = this.photo.numOfLikes + 1;
        }
      },
      error: () => {
      }
    })
  }

}
