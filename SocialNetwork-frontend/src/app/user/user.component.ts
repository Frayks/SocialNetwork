import {Component, HostListener, OnInit} from '@angular/core';
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
import {FriendsService} from "../shared/services/friends.service";
import {AddToFriendsConstants} from "../shared/constants/add-to-friends-constants";

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.scss']
})
export class UserComponent implements OnInit {

  userProfileInfo = new UserProfileInfo()
  showAdditionalInfo = false
  showPosts = true
  myProfile = false
  visible = false

  constructor(
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private authService: AuthService,
    private userService: UserService,
    private friendsService: FriendsService,
    private dialog: MatDialog
  ) {
  }

  ngOnInit(): void {
    if (!this.authService.authCredentials) {
      this.router.navigate(["/"])
    }
    this.activatedRoute.params.subscribe((params: Params) => {
      this.userService.loadUserProfileInfo(params['username']).subscribe({
        next: data => {
          this.userProfileInfo = data
          this.myProfile = data.username === this.authService.getUsername()
        },
        error: () => {
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

  addPhoto(input: any) {
    console.log("yes")
    //this.userService.addPhoto(input.files[0])
  }

  deletePhoto(photoId: number) {
    this.userService.deletePhoto(photoId).subscribe({
      next: () => {
        this.userProfileInfo.userPhotoList = this.userProfileInfo.userPhotoList.filter(item => item.id !== photoId)
      },
      error: () => {

      }
    })
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

  deletePost(postId: number) {
    this.userService.deletePost(postId).subscribe({
      next: () => {
        this.userProfileInfo.userPostList = this.userProfileInfo.userPostList.filter(item => item.id !== postId)
      },
      error: () => {

      }
    })
  }

  createFriendRequest(userId: number) {
    this.friendsService.createFriendRequest(userId).subscribe({
      next: data => {
        if (data.status === AddToFriendsConstants.ADDED) {
          this.userProfileInfo.friend = true
        } else if (data.status === AddToFriendsConstants.REQUEST_CREATED) {
          this.userProfileInfo.requestToFriends = true
        }
      },
      error: () => {

      }
    })
  }

  cancelFriendRequest(userId: number) {
    this.friendsService.cancelFriendRequest(userId).subscribe({
      next: () => {
        this.userProfileInfo.requestToFriends = false
      },
      error: () => {

      }
    })
  }

  @HostListener('window:scroll', ['$event'])
  scrollHandler(event: any) {
    let pos = window.scrollY;
    if (pos > 50) {
      this.visible = true
    } else {
      this.visible = false
    }
  }

  goUp() {
    window.scroll(0, 0);
  }

}
