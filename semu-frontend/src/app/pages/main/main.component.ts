import { Component } from '@angular/core';

@Component({
  selector: 'app-main',
  templateUrl: './main.component.html',
  styleUrls: ['./main.component.scss']
})
export class MainComponent {

  _sidebarOpen = false;

  openToggled($event: boolean): void {
    this._sidebarOpen = $event;
  }

  get isMobile(): boolean {
    return window.innerWidth <= 768;
  }



}
