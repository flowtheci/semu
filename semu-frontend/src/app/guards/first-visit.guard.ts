import { Injectable } from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree} from '@angular/router';
import { Observable } from 'rxjs';
import {AuthService} from "../service/auth.service";
import {environment} from "../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class FirstVisitGuard implements CanActivate {

  constructor(private router: Router, private authService: AuthService) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {

    if (!environment.production) {
      return true;
    }

    if (!this.authService.loggedIn) {
      return this.router.createUrlTree(['/log-in']);
    }

    if (!this.authService.validateJwt()) {
      this.authService.logOut();
      localStorage.removeItem('authToken');
      return this.router.createUrlTree(['/log-in']);
    }

    return true;
  }


}






