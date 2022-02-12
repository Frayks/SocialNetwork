import {Component, HostListener, Inject, OnInit} from '@angular/core';
import {News} from "../shared/models/News";
import {Router} from "@angular/router";
import {AuthService} from "../shared/services/auth.service";
import {NewsService} from "../shared/services/news.service";
import {UserPost} from "../shared/models/UserPost";
import {ViewPostPhotoDialogComponent} from "../view-post-photo-dialog/view-post-photo-dialog.component";
import {MatDialog} from "@angular/material/dialog";
import {DOCUMENT} from "@angular/common";

@Component({
  selector: 'app-news',
  templateUrl: './news.component.html',
  styleUrls: ['./news.component.scss']
})
export class NewsComponent implements OnInit {

  username!: string
  news = new News()
  visible = false

  constructor(
    private router: Router,
    private authService: AuthService,
    private newsService: NewsService,
    private dialog: MatDialog
  ) {
    if (!this.authService.authCredentials) {
      this.router.navigate(["/"])
    }
    this.username = authService.getUsername();
    this.refreshNews()
  }

  ngOnInit(): void {
  }

  viewPostPhoto(post: UserPost) {
    let dialogRef = this.dialog.open(ViewPostPhotoDialogComponent, {
      panelClass: 'dialog-container-cst',
      data: post
    });
  }

  refreshNews() {
    this.newsService.loadNews(this.username).subscribe({
      next: data => {
        this.news = data
      },
      error: error => {

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
