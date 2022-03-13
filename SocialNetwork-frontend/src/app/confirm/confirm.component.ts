import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Params, Router} from "@angular/router";
import {AuthService} from "../shared/services/auth.service";
import {MessageDataService} from "../shared/services/message-data.service";

@Component({
  selector: 'app-confirm',
  templateUrl: './confirm.component.html',
  styleUrls: ['./confirm.component.scss']
})
export class ConfirmComponent implements OnInit {

  constructor(
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private authService: AuthService,
    private messageDataService: MessageDataService
  ) {
    this.activatedRoute.params.subscribe((params: Params) => {
      authService.confirm(params['key']).subscribe({
        next: data => {
          messageDataService.title = "Вітаю!"
          messageDataService.text = "Акаунт успішно створено."
          router.navigate(['/message'])
        },
        error: error => {
          router.navigate([''])
        }
      })
    })
  }

  ngOnInit(): void {
  }

}
