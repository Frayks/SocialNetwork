import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {environment} from "../../../environments/environment";
import {EndpointConstants} from "../constants/endpoint-constants";
import {UserProfileInfo} from "../models/user-profile-info";
import {UserPhoto} from "../models/user-photo";
import {MenuData} from "../models/menu-data";
import {SearchResult} from "../models/search-result";
import {Post} from "../models/post";
import {FormStatus} from "../models/form-status";

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

  loadUserPostList(username: string) {
    let params = new HttpParams()
      .set('username', username);
    return this.httpClient.get<Post[]>(environment.server_url + EndpointConstants.GET_USER_POST_LIST_ENDPOINT, {
      params: params
    })
  }

  loadUserPostListBeforeTime(username: string, beforeTime: string) {
    let params = new HttpParams()
      .set('username', username)
      .set('beforeTime', beforeTime);
    return this.httpClient.get<Post[]>(environment.server_url + EndpointConstants.GET_USER_POST_LIST_ENDPOINT, {
      params: params
    })
  }

  loadMenuData() {
    return this.httpClient.get<MenuData>(environment.server_url + EndpointConstants.GET_MENU_DATA_ENDPOINT)
  }

  addPhoto(payload: FormData) {
    return this.httpClient.post<UserPhoto>(environment.server_url + EndpointConstants.ADD_PHOTO_ENDPOINT, payload);
  }

  deletePhoto(photoId: number) {
    let params = new HttpParams()
      .set('photoId', photoId);
    return this.httpClient.get(environment.server_url + EndpointConstants.DELETE_PHOTO_ENDPOINT, {
      params: params
    })
  }

  changePhotoLike(photoId: number) {
    let params = new HttpParams()
      .set('photoId', photoId);
    return this.httpClient.get(environment.server_url + EndpointConstants.CHANGE_PHOTO_LIKE_ENDPOINT, {
      params: params
    })
  }

  createPost(payload: FormData) {
    return this.httpClient.post<FormStatus>(environment.server_url + EndpointConstants.CREATE_POST_ENDPOINT, payload);
  }

  deletePost(postId: number) {
    let params = new HttpParams()
      .set('postId', postId);
    return this.httpClient.get(environment.server_url + EndpointConstants.DELETE_POST_ENDPOINT, {
      params: params
    })
  }

  changePostLike(postId: number) {
    let params = new HttpParams()
      .set('postId', postId);
    return this.httpClient.get(environment.server_url + EndpointConstants.CHANGE_POST_LIKE_ENDPOINT, {
      params: params
    })
  }

  searchUsers(searchRequest: string) {
    let params = new HttpParams()
      .set('searchRequest', searchRequest);
    return this.httpClient.get<SearchResult>(environment.server_url + EndpointConstants.SEARCH_USERS_ENDPOINT, {
      params: params
    })
  }

  deleteAccount(password: any) {
    return this.httpClient.post<FormStatus>(environment.server_url + EndpointConstants.DELETE_ACCOUNT, password);
  }
}
