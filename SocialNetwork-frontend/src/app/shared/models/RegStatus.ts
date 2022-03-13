import {RegFormField} from "../constants/reg-form-field";
import {RegStatusCode} from "../constants/reg-status-code";

export class RegStatus {
  status!: RegStatusCode
  invalidFieldsMap: any
}
