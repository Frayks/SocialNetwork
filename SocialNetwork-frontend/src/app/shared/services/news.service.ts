import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {environment} from "../../../environments/environment";
import {EndpointConstants} from "../constants/endpoint-constants";
import {Post} from "../models/post";

@Injectable({
  providedIn: 'root'
})
export class NewsService {

  constructor(private httpClient: HttpClient) {
  }

  loadPostList() {
    return this.httpClient.get<Post[]>(environment.server_url + EndpointConstants.GET_POST_LIST_BLOCK_ENDPOINT)
  }

  loadPostListBeforeTime(beforeTime: string) {
    let params = new HttpParams()
      .set('beforeTime', beforeTime);
    return this.httpClient.get<Post[]>(environment.server_url + EndpointConstants.GET_POST_LIST_BLOCK_ENDPOINT, {
      params: params
    })
  }

}
