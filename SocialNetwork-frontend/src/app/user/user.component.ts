import {Component, OnInit} from '@angular/core';
import {AuthService} from "../shared/services/auth.service";
import {ActivatedRoute, Params, Router} from "@angular/router";
import {UserService} from "../shared/services/user.service";
import {UserProfileInfo} from "../shared/models/UserProfileInfo";
import {MatDialog} from "@angular/material/dialog";
import {CreatePostDialogComponent} from "../create-post-dialog/create-post-dialog.component";
import {UserPhoto} from "../shared/models/UserPhoto";
import {ViewPhotoDialogComponent} from "../view-photo-dialog/view-photo-dialog.component";
import {UserPost} from "../shared/models/UserPost";
import {ViewPostPhotoDialogComponent} from "../view-post-photo-dialog/view-post-photo-dialog.component";

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.scss']
})
export class UserComponent implements OnInit {

  userProfileInfo = new UserProfileInfo()

  showAdditionalInfo = false;
  showPosts = true;
  myProfile = false;

  constructor(private router: Router, private activatedRoute: ActivatedRoute, private authService: AuthService, private userService: UserService, private dialog: MatDialog) {
  }

  ngOnInit(): void {
    if (!this.authService.authCredentials) {
      this.router.navigate(["/"])
    }
    this.activatedRoute.params.subscribe((params: Params) => {
      this.userService.loadUserProfileInfo(params['username']).subscribe({
        next: data => {
          this.userProfileInfo = data
          console.dir(data)
          this.myProfile = data.username === this.authService.getUsername()
        },
        error: error => {
          this.router.navigate([`users/${this.authService.getUsername()}`])
        }
      })
    })
  }

  toggleShowAdditionalInfo() {
    this.showAdditionalInfo = !this.showAdditionalInfo;
  }

  toggleShowPosts() {
    this.showPosts = !this.showPosts;
  }

  calcAge() {
    let date = Date.parse(this.userProfileInfo.dateOfBirth)
    let timeDiff = Math.abs(Date.now() - new Date(date).getTime())
    return Math.floor(timeDiff / (1000 * 3600 * 24) / 365.25)
  }

  addPhoto(input: any) {
    console.log("yes")
    //this.userService.addPhoto(input.files[0])
  }

  viewPhoto(photo: UserPhoto) {
    let dialogRef = this.dialog.open(ViewPhotoDialogComponent, {
      panelClass: 'dialog-container-cst',
      data: photo
    });
  }

  viewPostPhoto(post: UserPost) {
    let dialogRef = this.dialog.open(ViewPostPhotoDialogComponent, {
      panelClass: 'dialog-container-cst',
      data: post
    });
  }

  createPost() {
    let dialogRef = this.dialog.open(CreatePostDialogComponent, {
      panelClass: 'dialog-container-cst',
      data: null
    });
    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        console.dir(result)
        //this.userService.createPost(result).subscribe()
      }
    })
  }

  deletePhoto(photoId: number) {
    this.userProfileInfo.userPhotoList = this.userProfileInfo.userPhotoList.filter(item => item.id !== photoId)
    this.userService.deletePhoto(photoId).subscribe()
  }

  deletePost(postId: number) {
    this.userProfileInfo.userPostList = this.userProfileInfo.userPostList.filter(item => item.id !== postId)
    this.userService.deletePost(postId).subscribe()
  }

}
