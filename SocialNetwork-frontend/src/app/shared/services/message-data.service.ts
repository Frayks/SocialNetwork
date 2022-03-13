import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class MessageDataService {

  title!: string
  text!: string

  constructor() { }
}
