import {Component, HostListener, OnInit} from '@angular/core';
import {Router} from "@angular/router";
import {AuthService} from "../shared/services/auth.service";
import {NewsService} from "../shared/services/news.service";
import {UserPost} from "../shared/models/user-post";
import {ViewPostPhotoDialogComponent} from "../view-post-photo-dialog/view-post-photo-dialog.component";
import {MatDialog} from "@angular/material/dialog";
import {MenuData} from "../shared/models/menu-data";
import {UserService} from "../shared/services/user.service";
import {Post} from "../shared/models/post";

@Component({
  selector: 'app-news',
  templateUrl: './news.component.html',
  styleUrls: ['./news.component.scss']
})
export class NewsComponent implements OnInit {

  username!: string
  postList!: Post[]
  menuData = new MenuData()
  hideUpButton = true
  hideMoreButton = false

  constructor(
    private router: Router,
    private authService: AuthService,
    private newsService: NewsService,
    private dialog: MatDialog,
    private userService: UserService
  ) {
  }

  ngOnInit(): void {
    if (!this.authService.authCredentials) {
      this.router.navigate(["/"])
    }
    this.username = this.authService.getUsername()
    this.loadPostList()
    this.userService.loadMenuData().subscribe({
        next: data => {
          this.menuData = data
        },
        error: () => {

        }
      }
    )
  }

  viewPostPhoto(post: UserPost) {
    let dialogRef = this.dialog.open(ViewPostPhotoDialogComponent, {
      panelClass: 'dialog-container-cst',
      data: post
    });
  }

  changePostLike(post: Post) {
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

  loadPostList() {
    this.newsService.loadPostList().subscribe({
      next: data => {
        this.hideMoreButton = data.length == 0
        this.postList = data
      },
      error: error => {

      }
    })
  }

  loadPostListBlock() {
    let beforeTime = this.postList[this.postList.length - 1].creationTime
    console.log(beforeTime)
    this.newsService.loadPostListBeforeTime(beforeTime).subscribe({
      next: data => {
        this.hideMoreButton = data.length == 0
        this.postList = this.postList.concat(data)
      },
      error: error => {

      }
    })
  }

  @HostListener('window:scroll')
  scrollHandler() {
    let pos = window.scrollY;
    this.hideUpButton = pos < 50
  }

  goUp() {
    window.scroll(0, 0);
  }
}
