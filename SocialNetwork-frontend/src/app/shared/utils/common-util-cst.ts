import {FormStatus} from "../models/form-status";
import {FormFields} from "../constants/form-fields";
import {environment} from "../../../environments/environment";

export default class CommonUtilCst {

  public static readonly DAYS = Array.from({length: 31}, (_, i) => i + 1)
  public static readonly MONTHS = ['Січня', 'Лютого', 'Березня', 'Квітня', 'Травня', 'Червня', 'Липня', 'Серпня', 'Вересня', 'Жовтня', 'Листопада', 'Грудня']
  public static readonly YEARS = Array.from({length: environment.maxDateDiff + 1}, (_, i) => i + new Date().getFullYear() - environment.maxDateDiff).reverse()

  public static updateForm(form: any, formStatus: FormStatus) {
    let errorMessages = new Map<string, string>();
    if (formStatus.invalidFieldsMap) {
      if (formStatus.invalidFieldsMap[FormFields.FIRST_NAME]) {
        form.controls.firstName.setErrors({"failed_validation": true})
        errorMessages.set('firstName', formStatus.invalidFieldsMap[FormFields.FIRST_NAME])
      }
      if (formStatus.invalidFieldsMap[FormFields.LAST_NAME]) {
        form.controls.lastName.setErrors({"failed_validation": true})
        errorMessages.set('lastName', formStatus.invalidFieldsMap[FormFields.LAST_NAME])
      }
      if (formStatus.invalidFieldsMap[FormFields.EMAIL]) {
        form.controls.email.setErrors({"failed_validation": true})
        errorMessages.set('email', formStatus.invalidFieldsMap[FormFields.EMAIL])
      }
      if (formStatus.invalidFieldsMap[FormFields.USERNAME]) {
        form.controls.username.setErrors({"failed_validation": true})
        errorMessages.set('username', formStatus.invalidFieldsMap[FormFields.USERNAME])
      }
      if (formStatus.invalidFieldsMap[FormFields.PASSWORD]) {
        form.controls.password.setErrors({"failed_validation": true})
        errorMessages.set('password', formStatus.invalidFieldsMap[FormFields.PASSWORD])
      }
      if (formStatus.invalidFieldsMap[FormFields.DATE_OF_BIRTH]) {
        form.controls.dayOfBirth.setErrors({"failed_validation": true})
        form.controls.monthOfBirth.setErrors({"failed_validation": true})
        form.controls.yearOfBirth.setErrors({"failed_validation": true})
        errorMessages.set('dateOfBirth', formStatus.invalidFieldsMap[FormFields.DATE_OF_BIRTH])
      }
      if (formStatus.invalidFieldsMap[FormFields.SEX]) {
        form.controls.sex.setErrors({"failed_validation": true})
        errorMessages.set('sex', formStatus.invalidFieldsMap[FormFields.SEX])
      }
      if (formStatus.invalidFieldsMap[FormFields.ABOUT_YOURSELF]) {
        form.controls.aboutYourself.setErrors({"failed_validation": true})
        errorMessages.set('aboutYourself', formStatus.invalidFieldsMap[FormFields.ABOUT_YOURSELF])
      }
      if (formStatus.invalidFieldsMap[FormFields.CITY]) {
        form.controls.city.setErrors({"failed_validation": true})
        errorMessages.set('city', formStatus.invalidFieldsMap[FormFields.CITY])
      }
      if (formStatus.invalidFieldsMap[FormFields.SCHOOL]) {
        form.controls.school.setErrors({"failed_validation": true})
        errorMessages.set('school', formStatus.invalidFieldsMap[FormFields.SCHOOL])
      }
      if (formStatus.invalidFieldsMap[FormFields.UNIVERSITY]) {
        form.controls.university.setErrors({"failed_validation": true})
        errorMessages.set('university', formStatus.invalidFieldsMap[FormFields.UNIVERSITY])
      }
      if (formStatus.invalidFieldsMap[FormFields.RESTORE_KEY]) {
        form.controls.restoreKey.setErrors({"failed_validation": true})
        errorMessages.set('restoreKey', formStatus.invalidFieldsMap[FormFields.RESTORE_KEY])
      }
      if (formStatus.invalidFieldsMap[FormFields.POST_TEXT]) {
        form.controls.postText.setErrors({"failed_validation": true})
        errorMessages.set('postText', formStatus.invalidFieldsMap[FormFields.POST_TEXT])
      }
      if (formStatus.invalidFieldsMap[FormFields.CHAT_MESSAGE_TEXT]) {
        form.controls.chatMessageText.setErrors({"failed_validation": true})
        errorMessages.set('chatMessageText', formStatus.invalidFieldsMap[FormFields.CHAT_MESSAGE_TEXT])
      }
      if (formStatus.invalidFieldsMap[FormFields.IMAGE]) {
        form.controls.fileInput.setErrors({"failed_validation": true})
        errorMessages.set('fileInput', formStatus.invalidFieldsMap[FormFields.IMAGE])
      }
      if (formStatus.invalidFieldsMap[FormFields.ALL_FIELDS]) {
        form.controls.fileInput.setErrors({"failed_validation": true})
        errorMessages.set('allFields', formStatus.invalidFieldsMap[FormFields.ALL_FIELDS])
      }
    }
    return errorMessages
  }

  public static validateDateOfBirth(form: any, errorMessages: Map<string, string>) {
    if (form.value.dayOfBirth && form.value.monthOfBirth && form.value.yearOfBirth) {
      form.controls.dayOfBirth.touched = true
      form.controls.monthOfBirth.touched = true
      form.controls.yearOfBirth.touched = true
      if (!this.checkDateOfBirth(this.getDateString(form.value.monthOfBirth, form.value.dayOfBirth, form.value.yearOfBirth))) {
        form.controls.dayOfBirth.setErrors({"failed_validation": true})
        form.controls.monthOfBirth.setErrors({"failed_validation": true})
        form.controls.yearOfBirth.setErrors({"failed_validation": true})
      } else {
        form.controls.dayOfBirth.setErrors(null)
        form.controls.monthOfBirth.setErrors(null)
        form.controls.yearOfBirth.setErrors(null)
        errorMessages.delete('dateOfBirth')
      }
    }
  }

  public static checkDateOfBirth(dateOfBirth: string) {
    let date = new Date(dateOfBirth)
    let dateNow = new Date()
    return !isNaN(date.getDate()) && date.getTime() < dateNow.getTime() && dateNow.getFullYear() - date.getFullYear() <= environment.maxDateDiff
  }

  public static getDateString(monthOfBirth: string, dayOfBirth: string, yearOfBirth: string) {
    return monthOfBirth + "." + dayOfBirth + "." + yearOfBirth
  }

  public static mapToMb(size: number): number {
    return size / 1048576
  }

  public static getDateFormat(creationTime: string) {
    let date = new Date(creationTime)
    date.setHours(0, 0, 0, 0);
    let dateNow = new Date()
    dateNow.setHours(0, 0, 0, 0);
    return date.getTime() < dateNow.getTime() ? 'dd.MM.yyyy H:mm' : 'H:mm'
  }

}
