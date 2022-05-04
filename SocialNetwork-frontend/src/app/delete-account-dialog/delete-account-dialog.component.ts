import {Component, OnInit} from '@angular/core';
import {MatDialogRef} from "@angular/material/dialog";
import {StatusCode} from "../shared/constants/status-code";
import CommonUtilCst from "../shared/utils/common-util-cst";
import {UserService} from "../shared/services/user.service";

@Component({
  selector: 'app-delete-account-dialog',
  templateUrl: './delete-account-dialog.component.html',
  styleUrls: ['./delete-account-dialog.component.scss']
})
export class DeleteAccountDialogComponent implements OnInit {

  errorMessages = new Map<string, string>();

  constructor(
    private dialogRef: MatDialogRef<DeleteAccountDialogComponent>,
    private userService: UserService,
  ) {
  }

  ngOnInit(): void {
  }

  onSubmit(form: any) {
    if (form.valid) {
      this.userService.deleteAccount(form.value.password).subscribe({
        next: data => {
          if (data.status == StatusCode.FAILURE) {
            this.errorMessages = CommonUtilCst.updateForm(form, data)
          } else if (data.status == StatusCode.SUCCESS) {
            this.dialogRef.close(true)
          } else {
            this.dialogRef.close()
          }
        },
        error: () => {
        }
      })
    } else {
      form.controls.password.touched = true
    }
  }

  cancelDelete() {
    this.dialogRef.close()
  }

}
