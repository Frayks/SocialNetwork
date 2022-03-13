import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {environment} from "../../../environments/environment";
import {EndpointConstants} from "../constants/endpoint-constants";
import {UserProfileInfo} from "../models/UserProfileInfo";
import {UserPost} from "../models/UserPost";
import {UserPhoto} from "../models/UserPhoto";
import {MenuData} from "../models/MenuData";

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(private httpClient: HttpClient) {
  }

  loadUserProfileInfo(username: string) {
    let params = new HttpParams()
      .set('username', username);
    return this.httpClient.get<UserProfileInfo>(environment.server_url + EndpointConstants.GET_USER_PROFILE_INFO_ENDPOINT, {
      params: params
    })
  }

  loadMenuData() {
    return this.httpClient.get<MenuData>(environment.server_url + EndpointConstants.GET_MENU_DATA)
  }

  addPhoto(payload: FormData) {
    return this.httpClient.post<UserPhoto>(environment.server_url + EndpointConstants.ADD_PHOTO_ENDPOINT, payload);
  }

  createPost(payload: FormData) {
    return this.httpClient.post<UserPost>(environment.server_url + EndpointConstants.CREATE_POST_ENDPOINT, payload);
  }

  deletePhoto(photoId: number) {
    let params = new HttpParams()
      .set('photoId', photoId);
    return this.httpClient.get(environment.server_url + EndpointConstants.DELETE_PHOTO_ENDPOINT, {
      params: params
    })
  }

  deletePost(postId: number) {
    let params = new HttpParams()
      .set('postId', postId);
    return this.httpClient.get(environment.server_url + EndpointConstants.DELETE_POST_ENDPOINT, {
      params: params
    })
  }



}
