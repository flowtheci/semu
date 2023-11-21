import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {backendUrl} from "../app.component";
import {Router} from "@angular/router";

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(private http: HttpClient, private router: Router) { }

  _isLoading = false;

  get jwtToken() {
    return localStorage.getItem('authToken');
  }

  get loggedIn() {
    return !!this.jwtToken;
  }

  get loading() {
    return this._isLoading;
  }

  logIn(email: string, password: string) {
    this.startLoading();
    const headers = {
      'Content-Type': 'application/json',
    }

    this.http.post(backendUrl + 'users/authenticate?email=' + email + '&password=' + password, {headers: headers}).subscribe((response: any) => {
      console.warn(response);
      localStorage.setItem('authToken', response.token);
      this.router.navigate(['/home']);
      this._isLoading = false;
    });
  }

  register(email: string, password: string, firstName: string, lastName: string) {
    this.startLoading();
    const headers = {
      'Content-Type': 'application/json',
    }

    const body = {
      email: email,
      passwordHash: password,
      firstName: firstName,
      lastName: lastName
    }

    this.http.post(backendUrl + 'users/register', body, {headers: headers}).subscribe((response: any) => {
      console.warn(response);
      localStorage.setItem('authToken', response.token);
      this.router.navigate(['/home']);
      this._isLoading = false;
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
