import { EventEmitter, Output, Component } from '@angular/core';

@Component({
  selector: 'app-side-bar',
  templateUrl: './side-bar.component.html',
  styleUrls: ['./side-bar.component.scss']
})
export class SideBarComponent {

  isOpen = false;
  @Output() isOpenChange = new EventEmitter<boolean>();

  toggleSideBar(): void {
    this.isOpen = !this.isOpen;
    this.isOpenChange.emit(this.isOpen);
  }




}
