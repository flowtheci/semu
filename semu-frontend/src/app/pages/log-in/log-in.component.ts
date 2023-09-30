import {Component, ViewChild} from '@angular/core';
import {AuthService} from "../../service/auth.service";

@Component({
  selector: 'app-log-in',
  templateUrl: './log-in.component.html',
  styleUrls: ['./log-in.component.scss']
})
export class LogInComponent {

  @ViewChild('email') email: any;
  @ViewChild('password') password: any;

  constructor(private authService: AuthService) { }


  logIn() {
    console.log(this.email.nativeElement.value, this.password.nativeElement.value);
    this.authService.logIn(this.email.nativeElement.value, this.password.nativeElement.value);

  }

}
