import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {backendUrl} from "../app.component";
import {Router} from "@angular/router";

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(private http: HttpClient, private router: Router) { }

  get jwtToken() {
    return localStorage.getItem('authToken');
  }

  get loggedIn() {
    return !!this.jwtToken;
  }

  logIn(email: string, password: string) {
    const headers = {
      'Content-Type': 'application/json',
    }

    this.http.post(backendUrl + 'users/authenticate?email=' + email + '&password=' + password, {headers: headers}).subscribe((response: any) => {
      console.warn(response);
      localStorage.setItem('authToken', response.token);
      this.router.navigate(['/home']);
    });
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


}
