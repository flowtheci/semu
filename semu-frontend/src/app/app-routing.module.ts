import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {MainComponent} from "./pages/main/main.component";
import {LogInComponent} from "./pages/log-in/log-in.component";

const routes: Routes = [
  {path: 'log-in', component: LogInComponent},
  {path: 'home', component: MainComponent},
  {path: '', redirectTo: '/log-in', pathMatch: 'full'},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
