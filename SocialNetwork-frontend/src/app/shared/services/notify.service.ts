import {Injectable} from '@angular/core';
import {Title} from "@angular/platform-browser";
import {environment} from "../../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class NotifyService {

  constructor(
    private title: Title
  ) {
  }

  notifySoundAndTitle(num: number) {
    let audio = new Audio("assets/sounds/sound.mp3")
    audio.play()
    this.title.setTitle("(" + num + ") Нове повідомлення")
  }

  notifySound() {
    let audio = new Audio("assets/sounds/sound.mp3")
    audio.play()
  }

  notifyTitle(num: number) {
    this.title.setTitle("(" + num + ") Нове повідомлення")
  }

  clearTitle() {
    this.title.setTitle(environment.title)
  }

}
