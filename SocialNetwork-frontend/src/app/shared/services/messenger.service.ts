import {Injectable} from '@angular/core';
import {environment} from "../../../environments/environment";
import {EndpointConstants} from "../constants/endpoint-constants";
import {HttpClient, HttpParams} from "@angular/common/http";
import {ChatMessage} from "../models/chat-message";
import {ChatInfoData} from "../models/chat-info-data";
import {Post} from "../models/post";

@Injectable({
  providedIn: 'root'
})
export class MessengerService {

  constructor(private httpClient: HttpClient) {
  }

  loadChatInfoData(chatWith: string) {
    let params = chatWith ? new HttpParams().set('chatWith', chatWith) : {}
    return this.httpClient.get<ChatInfoData>(environment.server_url + EndpointConstants.GET_CHAT_INFO_DATA_ENDPOINT, {
      params: params
    })
  }

  loadChatMessageListBeforeTime(chatId: number, beforeTime: string) {
    let params = new HttpParams()
      .set('chatId', chatId)
      .set('beforeTime', beforeTime);
    return this.httpClient.get<ChatMessage[]>(environment.server_url + EndpointConstants.GET_CHAT_MESSAGE_LIST_BLOCK_ENDPOINT, {
      params: params
    })
  }

  loadChatMessageList(chatId: number) {
    let params = new HttpParams()
      .set('chatId', chatId);
    return this.httpClient.get<ChatMessage[]>(environment.server_url + EndpointConstants.GET_CHAT_MESSAGE_LIST_BLOCK_ENDPOINT, {
      params: params
    })
  }

}
