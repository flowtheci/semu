import {EventEmitter, Output, Component, OnInit} from '@angular/core';
import {SemuService} from "../../service/semu.service";

@Component({
  selector: 'app-side-bar',
  templateUrl: './side-bar.component.html',
  styleUrls: ['./side-bar.component.scss']
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
    this.semuService.selectedConversation = conversationId;
  }

  toggleSideBar(): void {
    this.isOpen = !this.isOpen;
    this.isOpenChange.emit(this.isOpen);
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
