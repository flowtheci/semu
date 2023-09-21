import { Component } from '@angular/core';

@Component({
  selector: 'app-side-bar',
  templateUrl: './side-bar.component.html',
  styleUrls: ['./side-bar.component.scss']
})
export class SideBarComponent {

  isOpen: boolean = true;


  toggleSideBar(): void {
    this.isOpen = !this.isOpen;
  }

}
