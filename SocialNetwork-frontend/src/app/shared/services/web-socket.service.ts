import {Injectable} from '@angular/core';
import {AuthService} from "./auth.service";
import {Router} from "@angular/router";
import {WebSocketMessage} from "../models/web-socket-message";
import {environment} from "../../../environments/environment";
import {EndpointConstants} from "../constants/endpoint-constants";
import {WebSocketSessionKey} from "../models/web-socket-session-key";

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {

  webSocket!: WebSocket

  constructor(
    private authService: AuthService,
    private router: Router
  ) {
  }

  async initialize() {
    await this.validateSession()
    this.webSocket.addEventListener('close', (event) => {
      if (event.code == 1003) {
        this.authService.refreshToken().subscribe({
          next: () => {
            this.validateSession()
          },
          error: () => {
            this.logout()
          }
        })
      }
    });
  }

  async sendMessage(message: WebSocketMessage) {
    await this.validateSession()
    this.webSocket?.send(JSON.stringify(message))
  }

  async validateSession() {
    if (!this.webSocket || this.webSocket.readyState == WebSocket.CLOSED) {
      try {
        let data = <WebSocketSessionKey>await this.authService.loadWebSocketSessionKey().toPromise()
        this.webSocket = new WebSocket(environment.server_ws_url + EndpointConstants.WS_ENDPOINT + "?key=" + data.key)
      } catch (e) {
        console.error(e)
      }
    }
  }

  closeSession() {
    this.webSocket?.close()
  }

  private logout() {
    this.closeSession()
    this.authService.logout()
  }

}
