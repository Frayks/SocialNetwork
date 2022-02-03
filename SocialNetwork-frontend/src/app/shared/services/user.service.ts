import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";
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

  createPost(payload: FormData) {
    return this.httpClient.post(environment.server_url + EndpointConstants.CREATE_POST_ENDPOINT, payload);
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
