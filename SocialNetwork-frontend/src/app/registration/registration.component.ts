import { Component, OnInit } from '@angular/core';
import {Router} from "@angular/router";

@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.scss']
})
export class RegistrationComponent implements OnInit {

  private router: Router

  constructor(router: Router) {
    this.router = router
  }

  ngOnInit(): void {
  }

  onBackClick() {
    this.router.navigate([''])
  }
}
