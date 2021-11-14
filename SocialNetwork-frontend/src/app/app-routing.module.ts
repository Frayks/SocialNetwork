import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {LoginComponent} from "./login/login.component";
import {RegistrationComponent} from "./registration/registration.component";
import {RestoreComponent} from "./restore/restore.component";
import {RestoreMessageComponent} from "./restore-message/restore-message.component";
import {ResetPasswordComponent} from "./reset-password/reset-password.component";
import {UserComponent} from "./user/user.component";

const routes: Routes = [
  {path: '', component: LoginComponent},
  {path: 'registration', component: RegistrationComponent},
  {path: 'restore', component: RestoreComponent},
  {path: 'restoreMessage', component: RestoreMessageComponent},
  {path: 'resetPassword', component: ResetPasswordComponent},
  {path: 'users/user', component: UserComponent},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
