import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {environment} from "../../../environments/environment";
import {EndpointConstants} from "../constants/endpoint-constants";
import {UserFriendsInfo} from "../models/UserFriendsInfo";
import {ResponseStatus} from "../models/ResponseStatus";

@Injectable({
  providedIn: 'root'
})
export class FriendsService {

  constructor(private httpClient: HttpClient) {
  }

  loadUserFriendsInfo(username: string) {
    let params = new HttpParams()
      .set('username', username);
    return this.httpClient.get<UserFriendsInfo>(environment.server_url + EndpointConstants.GET_USER_FRIENDS_INFO_ENDPOINT, {
      params: params
    })
  }

  createFriendRequest(userId: number) {
    let params = new HttpParams()
      .set('userId', userId);
    return this.httpClient.get<ResponseStatus>(environment.server_url + EndpointConstants.CREATE_FRIEND_REQUEST_ENDPOINT, {
      params: params
    })
  }

  cancelFriendRequest(userId: number) {
    let params = new HttpParams()
      .set('userId', userId);
    return this.httpClient.get(environment.server_url + EndpointConstants.CANCEL_FRIEND_REQUEST_ENDPOINT, {
      params: params
    })
  }

  deleteFriend(userId: number) {
    let params = new HttpParams()
      .set('userId', userId);
    return this.httpClient.get(environment.server_url + EndpointConstants.DELETE_FRIEND_ENDPOINT, {
      params: params
    })
  }

  agreeFriendRequest(userId: number) {
    let params = new HttpParams()
      .set('userId', userId);
    return this.httpClient.get(environment.server_url + EndpointConstants.AGREE_FRIEND_REQUEST_ENDPOINT, {
      params: params
    })
  }

  rejectFriendRequest(userId: number) {
    let params = new HttpParams()
      .set('userId', userId);
    return this.httpClient.get(environment.server_url + EndpointConstants.REJECT_FRIEND_REQUEST_ENDPOINT, {
      params: params
    })
  }
}
