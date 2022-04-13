import {Injectable} from '@angular/core';
import {Title} from "@angular/platform-browser";
import {environment} from "../../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class NotifyService {

  public static readonly SOUND_TYPE_1 = "assets/sounds/sound1.mp3"
  public static readonly SOUND_TYPE_2 = "assets/sounds/sound2.mp3"

  constructor(
    private title: Title
  ) {
  }

  notifySoundAndTitle(num: number, soundPath: string) {
    let audio = new Audio(soundPath)
    audio.play()
    this.title.setTitle("(" + num + ") Нове повідомлення")
  }

  notifySound(soundPath: string) {
    let audio = new Audio(soundPath)
    audio.play()
  }

  notifyTitle(num: number) {
    this.title.setTitle("(" + num + ") Нове повідомлення")
  }

  clearTitle() {
    this.title.setTitle(environment.title)
  }

}
