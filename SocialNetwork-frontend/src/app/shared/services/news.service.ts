import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {UserProfileInfo} from "../models/UserProfileInfo";
import {environment} from "../../../environments/environment";
import {EndpointConstants} from "../constants/endpoint-constants";
import {Post} from "../models/Post";
import {News} from "../models/News";

@Injectable({
  providedIn: 'root'
})
export class NewsService {

  constructor(private httpClient: HttpClient) {
  }

  loadNews(username: string) {
    let params = new HttpParams()
      .set('username', username);
    return this.httpClient.get<News>(environment.server_url + EndpointConstants.GET_NEWS_ENDPOINT, {
      params: params
    })
  }

}
