import {Component, HostListener, OnInit} from '@angular/core';
import {AuthService} from "../shared/services/auth.service";
import {ActivatedRoute, Params, Router} from "@angular/router";
import {UserService} from "../shared/services/user.service";
import {UserProfileInfo} from "../shared/models/user-profile-info";
import {MatDialog} from "@angular/material/dialog";
import {CreatePostDialogComponent} from "../create-post-dialog/create-post-dialog.component";
import {UserPhoto} from "../shared/models/user-photo";
import {ViewPhotoDialogComponent} from "../view-photo-dialog/view-photo-dialog.component";
import {UserPost} from "../shared/models/user-post";
import {ViewPostPhotoDialogComponent} from "../view-post-photo-dialog/view-post-photo-dialog.component";
import {FriendsService} from "../shared/services/friends.service";
import {AddToFriendsStatusCode} from "../shared/constants/add-to-friends-status-code";
import {MenuData} from "../shared/models/menu-data";
import {CommonUtil} from "../shared/Utils/common-util";

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.scss']
})
export class UserComponent implements OnInit {

  userProfileInfo = new UserProfileInfo()
  menuData = new MenuData()
  userPostList!: UserPost[]
  showAdditionalInfo = false
  showPosts = true
  myProfile = false
  username!: string
  hideUpButton = true
  hideMoreButton = false

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
      this.username = params['username']
      this.userService.loadUserProfileInfo(this.username).subscribe({
        next: data => {
          this.userProfileInfo = data
          this.myProfile = data.username === this.authService.getUsername()
        },
        error: () => {
          this.router.navigate([`users/${this.authService.getUsername()}`])
        }
      })
      this.loadUserPostList()
      this.userService.loadMenuData().subscribe({
          next: data => {
            this.menuData = data
          },
          error: () => {

          }
        }
      )
    })
  }

  loadUserPostList() {
    this.userService.loadUserPostList(this.username).subscribe({
      next: data => {
        this.hideMoreButton = data.length == 0
        this.userPostList = data
      },
      error: error => {

      }
    })
  }

  loadUserPostListBlock() {
    let beforeTime = this.userPostList[this.userPostList.length - 1].creationTime
    this.userService.loadUserPostListBeforeTime(this.username, beforeTime).subscribe({
      next: data => {
        this.hideMoreButton = data.length == 0
        this.userPostList = this.userPostList.concat(data)
      },
      error: error => {

      }
    })
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
    let photo = input.files[0];
    if (CommonUtil.mapToMb(photo.size) < 2) {
      let payload = new FormData()
      payload.append("photo", photo, photo.name)
      this.userService.addPhoto(payload).subscribe({
        next: data => {
          this.userProfileInfo.userPhotoList.unshift(data)
        },
        error: () => {

        }
      })
    }
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
        this.userService.createPost(result).subscribe(
          {
            next: data => {
              this.userPostList.unshift(data)
            },
            error: () => {

            }
          }
        )
      }
    })
  }

  deletePost(postId: number) {
    this.userService.deletePost(postId).subscribe({
      next: () => {
        this.userPostList = this.userPostList.filter(item => item.id !== postId)
      },
      error: () => {

      }
    })
  }

  changePostLike(post: UserPost) {
    this.userService.changePostLike(post.id).subscribe({
      next: () => {
        if (post.like) {
          post.like = false;
          post.numOfLikes = post.numOfLikes - 1;
        } else {
          post.like = true;
          post.numOfLikes = post.numOfLikes + 1;
        }
      },
      error: () => {
      }
    })
  }

  createFriendRequest(userId: number) {
    this.friendsService.createFriendRequest(userId).subscribe({
      next: data => {
        if (data.status === AddToFriendsStatusCode.ADDED) {
          this.userProfileInfo.friend = true
        } else if (data.status === AddToFriendsStatusCode.REQUEST_CREATED) {
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

  @HostListener('window:scroll', ['$event'])
  scrollHandler(event: any) {
    let pos = window.scrollY;
    this.hideUpButton = pos < 50
  }

  goUp() {
    window.scroll(0, 0);
  }

}
