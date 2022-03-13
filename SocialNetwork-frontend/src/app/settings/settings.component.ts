import {Component, OnInit} from '@angular/core';
import {UserService} from "../shared/services/user.service";
import {MenuData} from "../shared/models/MenuData";

@Component({
  selector: 'app-settings',
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.scss']
})
export class SettingsComponent implements OnInit {

  menuData = new MenuData()

  constructor(private userService: UserService) { }

  ngOnInit(): void {
    this.userService.loadMenuData().subscribe({
        next: data => {
          this.menuData = data
        },
        error: () => {

        }
      }
    )
  }

  onSubmit(form: any) {

  }
}
