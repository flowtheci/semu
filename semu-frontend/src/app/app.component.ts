import { Component } from '@angular/core';
import {ViewportScroller} from "@angular/common";
import {NavigationEnd, Router} from "@angular/router";


export const devMode = true;
export const backendUrl = devMode ? 'http://localhost:8080/api/' : 'https://tribal-saga-397814.lm.r.appspot.com/api/';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})

export class AppComponent {
  title = 'SEMU';

  constructor(private router: Router, private viewportScroller: ViewportScroller) {
    this.router.events.subscribe((event) => {
      if (event instanceof NavigationEnd) {
        this.viewportScroller.scrollToPosition([0,0]);
      }
    });
  }
}
