import { Component } from '@angular/core';


export const devMode = false;
export const backendUrl = devMode ? 'http://localhost:8080/api/' : 'https://tribal-saga-397814.lm.r.appspot.com/api/';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})

export class AppComponent {
  title = 'SEMU';
}
