import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {UserProfileInfo} from "../models/UserProfileInfo";
import {environment} from "../../../environments/environment";
import {EndpointConstants} from "../constants/endpoint-constants";
import {UserFriendsInfo} from "../models/UserFriendsInfo";

@Injectable({
  providedIn: 'root'
})
export class FriendsService {

  constructor(private httpClient: HttpClient) { }

  loadUserFriendsInfo(username: string) {
    let params = new HttpParams()
      .set('username', username);
    return this.httpClient.get<UserFriendsInfo>(environment.server_url + EndpointConstants.USER_FRIENDS_INFO_ENDPOINT, {
      params: params
    })
  }

}
