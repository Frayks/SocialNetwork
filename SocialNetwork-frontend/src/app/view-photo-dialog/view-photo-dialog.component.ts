import {Component, Inject, OnInit} from '@angular/core';
import {UserPhoto} from "../shared/models/UserPhoto";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";

@Component({
  selector: 'app-view-photo-dialog',
  templateUrl: './view-photo-dialog.component.html',
  styleUrls: ['./view-photo-dialog.component.scss']
})
export class ViewPhotoDialogComponent implements OnInit {

  photo: UserPhoto

  constructor(private dialogRef: MatDialogRef<ViewPhotoDialogComponent>, @Inject(MAT_DIALOG_DATA) photo: UserPhoto) {
    this.photo = photo
  }

  ngOnInit(): void {
  }

}
