import {Component, OnInit} from '@angular/core';
import {AuthService} from "../shared/services/auth.service";
import {Router} from "@angular/router";
import {UserService} from "../shared/services/user.service";
import {SearchResult} from "../shared/models/search-result";
import {WebSocketService} from "../shared/services/web-socket.service";

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {

  searchResult!: SearchResult

  constructor(
    private router: Router,
    private authService: AuthService,
    private userService: UserService,
    private webSocketService: WebSocketService
  ) {
  }

  ngOnInit(): void {
  }

  logout() {
    this.webSocketService.closeSession()
    this.authService.logout()
  }

  search(inputSearch: any) {
    let searchRequest = inputSearch.value
    if (searchRequest.length > 0) {
      this.userService.searchUsers(searchRequest).subscribe({
          next: data => {
            this.searchResult = data
          },
          error: () => {
          }
        }
      )
    } else {
      this.searchResult.userList = []
    }
  }

  clearSearchState(input: any) {
    setTimeout(() => {
      input.value = ""
      input.focus = false
      this.searchResult.userList = []
    }, 100);
  }

}
