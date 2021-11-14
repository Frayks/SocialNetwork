import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {RegistrationComponent} from './registration/registration.component';
import {LoginComponent} from './login/login.component';
import {UserComponent} from './user/user.component';
import {MenuComponent} from './menu/menu.component';
import {RestoreComponent} from './restore/restore.component';
import { ResetPasswordComponent } from './reset-password/reset-password.component';
import { RestoreMessageComponent } from './restore-message/restore-message.component';
import { HeaderComponent } from './header/header.component';

@NgModule({
  declarations: [
    AppComponent,
    RegistrationComponent,
    LoginComponent,
    UserComponent,
    MenuComponent,
    RestoreComponent,
    ResetPasswordComponent,
    RestoreMessageComponent,
    HeaderComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule {
}
