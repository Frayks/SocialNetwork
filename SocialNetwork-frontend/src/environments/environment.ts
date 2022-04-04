// This file can be replaced during build by using the `fileReplacements` array.
// `ng build` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

export const environment = {
  production: false,
  server_url: 'http://localhost:8080',
  server_ws_url: 'ws://localhost:8080',

  maxImageSize: 2,
  maxAboutYourselfTextLength: 50,
  maxPostTextLength: 1000,
  maxChatMessageTextLength: 1000,
  minPasswordLength: 8,
  maxDateDiff: 100

};

/*
 * For easier debugging in development mode, you can import the following file
 * to ignore zone related error stack frames such as `zone.run`, `zoneDelegate.invokeTask`.
 *
 * This import should be commented out in production mode because it will have a negative impact
 * on performance if an error is thrown.
 */
// import 'zone.js/plugins/zone-error';  // Included with Angular CLI.
