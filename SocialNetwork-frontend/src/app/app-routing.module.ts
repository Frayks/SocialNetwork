import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {LoginComponent} from "./login/login.component";
import {RegistrationComponent} from "./registration/registration.component";
import {RestoreComponent} from "./restore/restore.component";
import {RestoreMessageComponent} from "./restore-message/restore-message.component";
import {ResetPasswordComponent} from "./reset-password/reset-password.component";
import {UserComponent} from "./user/user.component";
import {FriendsComponent} from "./friends/friends.component";
import {NewsComponent} from "./news/news.component";
import {SettingsComponent} from "./settings/settings.component";

const routes: Routes = [
  {path: '', component: LoginComponent},
  {path: 'registration', component: RegistrationComponent},
  {path: 'restore', component: RestoreComponent},
  {path: 'restoreMessage', component: RestoreMessageComponent},
  {path: 'resetPassword', component: ResetPasswordComponent},
  {path: 'users/:username', component: UserComponent},
  {path: 'users/:username/friends', component: FriendsComponent},
  {path: 'news', component: NewsComponent},
  {path: 'settings', component: SettingsComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
