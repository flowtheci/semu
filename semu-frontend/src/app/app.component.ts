import { Component } from '@angular/core';

export const backendUrl = 'https://semu-api.fly.dev/api/'; // prod: https://semu-api.fly.dev/api/ dev: http://localhost:8080/api/

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})

export class AppComponent {
  title = 'semu';





}
