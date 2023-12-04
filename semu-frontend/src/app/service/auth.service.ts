import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Router} from "@angular/router";
import {environment} from "../../environments/environment";
import {ToastrService} from "ngx-toastr";

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(private http: HttpClient, private router: Router,  private toastr: ToastrService) { }

  _isLoading = false;

  get jwtToken() {
    return localStorage.getItem('authToken');
  }

  get loggedIn() {
    return this.validateJwt();
  }

  get loading() {
    return this._isLoading;
  }


  showLoginError() {
    this.toastr.error('E-mail või salasõna on vale!', 'Error', {
      timeOut: 3000,
      positionClass: 'toast-top-center',

    });
  }

  showRegisterError() {
    this.toastr.error('Kahjuks sul pole veel SEMU ligipääsu, või sinu SEMU võti oli vale. Kontrolli võtit ja proovi uuesti!', 'Error', {
      timeOut: 3000,
      positionClass: 'toast-top-center',
    });
  }

  showCommonError() {
    this.toastr.error('Sisselogimine ebaõnnestus. Proovi hiljem uuesti!', 'Error', {
      timeOut: 3000,
      positionClass: 'toast-top-center',
    });
  }

  logIn(email: string, password: string) {
    password = btoa(password);
    this.startLoading();
    const headers = {
      'Content-Type': 'application/json',
    }
    const body = {
      email: email,
      passwordHash: password,
    }

    this.http.post(environment.apiUrl + 'users/authenticate', body, {headers: headers}).subscribe((response: any) => {
      localStorage.setItem('authToken', response.token);
      this.router.navigate(['/home']);
      this._isLoading = false;
    },
      (error) => {
        this._isLoading = false;
        console.warn(error);
        if (error.status == 401) {
          this.showLoginError();
        } else {
          this.showCommonError();
        }
      });
  }

  register(email: string, password: string, firstName: string, lastName: string, accessKey: string) {
    password = btoa(password);
    this.startLoading();
    const headers = {
      'Content-Type': 'application/json',
    }

    const body = {
      email: email,
      passwordHash: password,
      firstName: firstName,
      lastName: lastName,
    }

    this.http.post(environment.apiUrl + 'users/register?key=' + accessKey, body, {headers: headers}).subscribe((response: any) => {
      console.warn(response);
      localStorage.setItem('authToken', response.token);
      this.router.navigate(['/home']);
      this._isLoading = false;
    },
      (error) => {
        console.error(error.token);
        this._isLoading = false;
        if (error.status == 401) {
          this.showRegisterError();
        } else {
          this.showCommonError();
        }
      });
  }

  startLoading() {
    this._isLoading = true;
    setTimeout(() => {
      this._isLoading = false;
    }
    , 30000);
  }


  logOut() {
    localStorage.removeItem('authToken');
  }

  validateJwt(): boolean {
    const jwt = this.jwtToken;
    if (!jwt) {
      return false;
    }

    const jwtData = jwt.split('.')[1];
    const decodedJwtJsonData = window.atob(jwtData);
    const decodedJwtData = JSON.parse(decodedJwtJsonData);
    const expirationDate = new Date(decodedJwtData.exp * 1000);
    const current = new Date();
    return current < expirationDate;
  }

  saveRateLimit(validUntil: string) {
    localStorage.setItem('rateLimit', validUntil);
  }

  getRateLimit(): string {
    return localStorage.getItem('rateLimit') || '';
  }

  deleteRateLimit() {
    localStorage.removeItem('rateLimit');
  }


}
