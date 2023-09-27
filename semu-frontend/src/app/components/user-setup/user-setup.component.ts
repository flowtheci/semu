import {Component, ElementRef, ViewChild} from '@angular/core';
import {Router} from "@angular/router";

@Component({
  selector: 'app-user-setup',
  templateUrl: './user-setup.component.html',
  styleUrls: ['./user-setup.component.scss']
})
export class UserSetupComponent {

  constructor(private router: Router) {
  }


  @ViewChild('firstName') firstName: ElementRef | undefined;
  @ViewChild('apiKey') apiKey: ElementRef | undefined;
  @ViewChild('userClass') userClass: ElementRef | undefined;

  save() {
    if ((!this.firstName || !this.apiKey || !this.userClass) || (!this.firstName.nativeElement.value || !this.apiKey.nativeElement.value || !this.userClass.nativeElement.value)) {
      return;
    }

    let firstName = this.firstName.nativeElement.value;
    const apiKey = this.apiKey.nativeElement.value;
    const userClass = this.userClass.nativeElement.value;
    firstName = firstName.charAt(0).toUpperCase() + firstName.slice(1).toLowerCase();

    localStorage.setItem('firstName', firstName);
    localStorage.setItem('apiKey', apiKey);
    localStorage.setItem('userClass', userClass);
    localStorage.setItem('firstVisit', 'true');

    this.router.navigate(['/home']);
  }

}
