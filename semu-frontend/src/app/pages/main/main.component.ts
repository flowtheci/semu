import { Component } from '@angular/core';
import {SemuService} from "../../service/semu.service";

@Component({
  selector: 'app-main',
  templateUrl: './main.component.html',
  styleUrls: ['./main.component.scss']
})
export class MainComponent {

  constructor(private semuService: SemuService) {
  }


  _sidebarOpen = false;

  openToggled($event: boolean): void {
    this._sidebarOpen = $event;
  }

  get isMobile(): boolean {
    return window.innerWidth <= 768;
  }

  get selectedConversationId(): string {
    return this.semuService.selectedConversation;
  }



}
