// This file can be replaced during build by using the `fileReplacements` array.
// `ng build` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

export const environment = {
  production: false,
  server_url: 'http://localhost:8080',
  server_ws_url: 'ws://localhost:8080',
  title: 'SocialNetwork',

  maxImageSize: 2,
  minFirstNameLength: 2,
  maxFirstNameLength: 15,
  minLastNameLength: 2,
  maxLastNameLength: 15,
  minUsernameLength: 2,
  maxUsernameLength: 20,
  minPasswordLength: 8,
  maxDateDiff: 100,
  maxAboutYourselfTextLength: 50,
  maxCityTextLength: 20,
  maxSchoolTextLength: 20,
  maxUniversityTextLength: 20,
  maxPostTextLength: 1000,
  maxChatMessageTextLength: 1000,

};

/*
 * For easier debugging in development mode, you can import the following file
 * to ignore zone related error stack frames such as `zone.run`, `zoneDelegate.invokeTask`.
 *
 * This import should be commented out in production mode because it will have a negative impact
 * on performance if an error is thrown.
 */
// import 'zone.js/plugins/zone-error';  // Included with Angular CLI.
