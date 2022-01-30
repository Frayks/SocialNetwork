import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {environment} from "../../../environments/environment";
import {EndpointConstants} from "../constants/endpoint-constants";
import {UserProfileInfo} from "../models/UserProfileInfo";

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(private httpClient: HttpClient) {
  }

  loadUserProfileInfo(username: string) {
    let params = new HttpParams()
      .set('username', username);
    return this.httpClient.get<UserProfileInfo>(environment.server_url + EndpointConstants.USER_PROFILE_INFO_ENDPOINT, {
      params: params
    })
  }


}
