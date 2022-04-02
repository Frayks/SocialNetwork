import {WebSocketMessageType} from "../constants/web-socket-message-type";

export class WebSocketMessage {
  type!: WebSocketMessageType
  body: any

  constructor(type: WebSocketMessageType) {
    this.type = type;
  }

}
