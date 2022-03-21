import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {UserPost} from "../shared/models/user-post";

@Component({
  selector: 'app-view-post-photo-dialog',
  templateUrl: './view-post-photo-dialog.component.html',
  styleUrls: ['./view-post-photo-dialog.component.scss']
})
export class ViewPostPhotoDialogComponent implements OnInit {

  post: UserPost

  constructor(
    private dialogRef: MatDialogRef<ViewPostPhotoDialogComponent>,
    @Inject(MAT_DIALOG_DATA) post: UserPost
  ) {
    this.post = post
  }

  ngOnInit(): void {
  }

}
