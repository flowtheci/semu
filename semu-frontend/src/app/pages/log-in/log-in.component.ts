import {Component, ViewChild} from '@angular/core';
import {AuthService} from "../../service/auth.service";
import {Router} from "@angular/router";
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-log-in',
  templateUrl: './log-in.component.html',
  styleUrls: ['./log-in.component.scss']
})
export class LogInComponent {

  @ViewChild('email') email: any;
  @ViewChild('password') password: any;
  @ViewChild('firstname') firstName: any;
  @ViewChild('lastname') lastName: any;
  @ViewChild('accessKey') accessKey: any;

  @ViewChild('emailLogin') emailLogin: any;
  @ViewChild('passwordLogin') passwordLogin: any;
  _isRegistering = false;

  constructor(private authService: AuthService, private router: Router) { }

  ngBeforeViewInit() {
    if (this.authService.loggedIn) {
      this.router.navigate(['/home']);
    }
  }

  get isRegisterForm() {
    return this._isRegistering;
  }

  get isLoading() {
    return this.authService.loading;
  }

  logIn() {
    this.authService.logIn(this.emailLogin.nativeElement.value, this.passwordLogin.nativeElement.value);
  }

  register() {
    this.authService.register(this.email.nativeElement.value, this.password.nativeElement.value, this.firstName.nativeElement.value, this.lastName.nativeElement.value, this.accessKey.nativeElement.value);
  }

  toggle() {
    this._isRegistering = !this._isRegistering;
  }

}
