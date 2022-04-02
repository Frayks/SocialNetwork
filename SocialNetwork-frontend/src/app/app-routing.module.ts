import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {LoginComponent} from "./login/login.component";
import {RegistrationComponent} from "./registration/registration.component";
import {RestoreComponent} from "./restore/restore.component";
import {MessageComponent} from "./message/message.component";
import {ResetPasswordComponent} from "./reset-password/reset-password.component";
import {UserComponent} from "./user/user.component";
import {FriendsComponent} from "./friends/friends.component";
import {NewsComponent} from "./news/news.component";
import {SettingsComponent} from "./settings/settings.component";
import {ConfirmComponent} from "./confirm/confirm.component";
import {MessengerComponent} from "./messenger/messenger.component";

const routes: Routes = [
  {path: '', component: LoginComponent},
  {path: 'registration', component: RegistrationComponent},
  {path: 'confirm/:key', component: ConfirmComponent},
  {path: 'restore', component: RestoreComponent},
  {path: 'message', component: MessageComponent},
  {path: 'resetPassword/:key', component: ResetPasswordComponent},
  {path: 'users/:username', component: UserComponent},
  {path: 'users/:username/friends', component: FriendsComponent},
  {path: 'news', component: NewsComponent},
  {path: 'settings', component: SettingsComponent},
  {path: 'messenger', component: MessengerComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
