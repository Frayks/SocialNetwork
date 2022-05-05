import {Injectable} from '@angular/core';
import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest, HttpStatusCode} from '@angular/common/http';
import {catchError, Observable, switchMap} from 'rxjs';
import {AuthService} from "../services/auth.service";
import {EndpointConstants} from "../constants/endpoint-constants";
import {Router} from "@angular/router";

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor(
    private authService: AuthService,
    private router: Router
  ) {
  }

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    let pathname = new URL(request.url).pathname
    if (!pathname.endsWith(EndpointConstants.LOGIN_ENDPOINT) &&
      !pathname.endsWith(EndpointConstants.REGISTRATION_ENDPOINT) &&
      !pathname.endsWith(EndpointConstants.REFRESH_TOKEN_ENDPOINT) &&
      !pathname.endsWith(EndpointConstants.RESTORE_ENDPOINT)
    ) {
      request = this.authService.addAuthHeader(request)
      return next.handle(request).pipe(
        catchError(error => {
          if (error.status == HttpStatusCode.Forbidden) {
            return this.authService.refreshToken().pipe(
              switchMap(() => {
                request = this.authService.addAuthHeader(request)
                return next.handle(request)
              }),
              catchError(() => {
                this.authService.logout()
                throw error
              })
            )
          } else {
            throw error
          }
        })
      )
    } else {
      return next.handle(request)
    }
  }

}
