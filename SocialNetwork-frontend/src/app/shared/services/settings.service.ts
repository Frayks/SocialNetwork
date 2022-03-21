import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {environment} from "../../../environments/environment";
import {EndpointConstants} from "../constants/endpoint-constants";
import {Settings} from "../models/settings";
import {FormStatus} from "../models/form-status";
import {AdditionalSettings} from "../models/additional-settings";

@Injectable({
  providedIn: 'root'
})
export class SettingsService {

  constructor(private httpClient: HttpClient) {
  }

  loadSettings() {
    return this.httpClient.get<Settings>(environment.server_url + EndpointConstants.GET_SETTINGS)
  }

  changeAdditionalSettings(additionalSettings: AdditionalSettings) {
    return this.httpClient.post<FormStatus>(environment.server_url + EndpointConstants.CHANGE_ADDITIONAL_SETTINGS_ENDPOINT, additionalSettings);
  }

  changeBasicSettings(payload: FormData) {
    return this.httpClient.post<FormStatus>(environment.server_url + EndpointConstants.CHANGE_BASIC_SETTINGS_ENDPOINT, payload);
  }

}
