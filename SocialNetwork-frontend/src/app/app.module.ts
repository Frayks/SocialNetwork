import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {RegistrationComponent} from './registration/registration.component';
import {LoginComponent} from './login/login.component';
import {UserComponent} from './user/user.component';
import {MenuComponent} from './menu/menu.component';
import {RestoreComponent} from './restore/restore.component';
import {ResetPasswordComponent} from './reset-password/reset-password.component';
import {RestoreMessageComponent} from './restore-message/restore-message.component';
import {HeaderComponent} from './header/header.component';
import {HTTP_INTERCEPTORS, HttpClientModule} from "@angular/common/http";
import {FormsModule} from '@angular/forms';
import {AuthInterceptor} from "./shared/interceptors/auth.interceptor";
import {JWT_OPTIONS, JwtHelperService, JwtModule} from "@auth0/angular-jwt";

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
    AppRoutingModule,
    FormsModule,
    HttpClientModule,
    JwtModule
  ],
  providers: [
    {provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true},
    {provide: JWT_OPTIONS, useValue: JWT_OPTIONS},
    JwtHelperService
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
