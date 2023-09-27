import {AfterViewInit, Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {Router} from "@angular/router";

@Component({
  selector: 'app-user-setup',
  templateUrl: './user-setup.component.html',
  styleUrls: ['./user-setup.component.scss']
})
export class UserSetupComponent implements AfterViewInit {

  constructor(private router: Router) {
  }


  @ViewChild('firstName') firstName: ElementRef | undefined;
  @ViewChild('apiKey') apiKey: ElementRef | undefined;
  @ViewChild('userClass') userClass: ElementRef | undefined;

  ngAfterViewInit(): void {
    if (localStorage.getItem('firstVisit') === 'true') {
      this.firstName?.nativeElement.setAttribute('value', localStorage.getItem('firstName') || '');
      this.apiKey?.nativeElement.setAttribute('value', localStorage.getItem('apiKey') || '');
      this.userClass?.nativeElement.setAttribute('value', localStorage.getItem('userClass') || '');
    }
  }



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
