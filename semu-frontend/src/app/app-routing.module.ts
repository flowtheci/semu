import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {MainComponent} from "./pages/main/main.component";
import {LogInComponent} from "./pages/log-in/log-in.component";
import {FirstVisitGuard} from "./guards/first-visit.guard";
import {UserSetupComponent} from "./components/user-setup/user-setup.component";

const routes: Routes = [
  {path: '', component: LogInComponent},
  {path: 'home', component: MainComponent, canActivate: [FirstVisitGuard]},
  {path: 'setup', component: UserSetupComponent, canActivate: [FirstVisitGuard]},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
