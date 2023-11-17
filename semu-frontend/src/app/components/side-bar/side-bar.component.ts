import {EventEmitter, Output, Component, OnInit} from '@angular/core';
import {SemuService} from "../../service/semu.service";
import { animate, state, style, transition, trigger } from '@angular/animations';

@Component({
  selector: 'app-side-bar',
  templateUrl: './side-bar.component.html',
  styleUrls: ['./side-bar.component.scss'],
})
export class SideBarComponent implements OnInit {

  isOpen = false;
  @Output() isOpenChange = new EventEmitter<boolean>();

  conversationTitles: any[] = [];

  selectedSubMenu = SubMenu.None;

  constructor(private semuService: SemuService) {
  }

  ngOnInit() {
    this.semuService.getConversationTitles(5).then((response: any) => {
      console.warn(response);
      this.conversationTitles = Object.entries(response).map(([key, value]) => ({key, value}));
      this.conversationTitles.reverse();
    }
    );
  }


  selectConversation(conversationId: string) {
    console.warn('selecting: ' + conversationId);
    this.isOpen = false;
    this.isOpenChange.emit(this.isOpen);
    setTimeout(() => {
      this.semuService.selectedConversation = conversationId;
    }, 300);
  }

  toggleSideBar(): void {
    this.isOpen = !this.isOpen;
    this.isOpenChange.emit(this.isOpen);
  }

  isIOS(): boolean {
    return /iPad|iPhone|iPod/.test(navigator.userAgent) && !(window as any).MSStream;
  }

  select(selection: SubMenu) {
    if (this.selectedSubMenu === selection) {
      this.selectedSubMenu = SubMenu.None;
    }
    else {
      this.selectedSubMenu = selection;
    }
  }

  logout() {
    localStorage.clear();
    window.location.reload();
  }



}

export enum SubMenu {
  None, Subjects, Conversations
}
