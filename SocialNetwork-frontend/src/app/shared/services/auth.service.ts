import {Injectable} from '@angular/core';
import {HttpClient, HttpParams, HttpRequest} from '@angular/common/http';
import {map} from "rxjs";
import {EndpointConstants} from "../constants/endpoint-constants";
import {environment} from "../../../environments/environment";
import {JwtConstants} from "../constants/jwt-constants";
import {JwtHelperService} from "@auth0/angular-jwt";
import {RegForm} from "../models/RegForm";
import {RegStatus} from "../models/RegStatus";

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(private httpClient: HttpClient, private jwtHelperService: JwtHelperService) {
  }

  login(username: string, password: string) {
    const body = new HttpParams()
      .set('username', username)
      .set('password', password);
    return this.httpClient.post<any>(environment.server_url + EndpointConstants.LOGIN_ENDPOINT, body)
      .pipe(map(data => {
        localStorage.setItem(JwtConstants.AUTH_CREDENTIALS_KEY, JSON.stringify(data))
      }))
  }

  registration(regForm: RegForm) {
    return this.httpClient.post<RegStatus>(environment.server_url + EndpointConstants.REGISTRATION_ENDPOINT, regForm);
  }

  confirm(key: string) {
    let params = new HttpParams()
      .set('key', key);
    return this.httpClient.get(environment.server_url + EndpointConstants.CONFIRM_ENDPOINT, {
      params: params
    });
  }

  logout() {
    let authCredentials = this.authCredentials
    this.httpClient.post<any>(environment.server_url + EndpointConstants.LOGOUT_ENDPOINT, authCredentials).subscribe()
    localStorage.removeItem(JwtConstants.AUTH_CREDENTIALS_KEY)
  }

  refreshToken() {
    let authCredentials = this.authCredentials
    let refresh_token = (authCredentials && authCredentials.refresh_token) ? authCredentials.refresh_token : null;
    return this.httpClient.get<any>(environment.server_url + EndpointConstants.REFRESH_TOKEN_ENDPOINT, {
      headers: {
        Authorization: JwtConstants.BEARER_PREFIX + refresh_token
      }
    }).pipe(map(data => {
      localStorage.setItem(JwtConstants.AUTH_CREDENTIALS_KEY, JSON.stringify(data))
    }))
  }

  addAuthHeader(request: HttpRequest<unknown>) {
    let authCredentials = this.authCredentials
    if (authCredentials && authCredentials.access_token) {
      request = request.clone({
        setHeaders: {
          Authorization: JwtConstants.BEARER_PREFIX + authCredentials.access_token
        }
      })
    }
    return request
  }

  get authCredentials() {
    let authCredentialsStr = localStorage.getItem(JwtConstants.AUTH_CREDENTIALS_KEY)
    return (authCredentialsStr) ? JSON.parse(authCredentialsStr) : null
  }

  getUsername() {
    return this.jwtHelperService.decodeToken(this.authCredentials.access_token).sub
  }

}
