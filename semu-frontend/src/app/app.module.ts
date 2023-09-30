import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LogInComponent } from './pages/log-in/log-in.component';
import { MainComponent } from './pages/main/main.component';
import { SideBarComponent } from './components/side-bar/side-bar.component';
import { ChatWindowComponent } from './pages/chat-window/chat-window.component';
import { TypewriterComponent } from './components/typewriter/typewriter.component';
import {HttpClientModule} from "@angular/common/http";
import { UserSetupComponent } from './components/user-setup/user-setup.component';
import { LogoComponent } from './components/logo/logo.component';
import {PromptUtil} from "./prompts";
import {JwtModule} from "@auth0/angular-jwt";

export function tokenGetter() {
  return localStorage.getItem("authToken");
}

@NgModule({
  declarations: [
    AppComponent,
    LogInComponent,
    MainComponent,
    SideBarComponent,
    ChatWindowComponent,
    TypewriterComponent,
    UserSetupComponent,
    LogoComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    JwtModule.forRoot({
      config: {
        tokenGetter: tokenGetter,
        allowedDomains: ["semu-api.fly.dev"],
      },
    }),
  ],
  providers: [PromptUtil],
  bootstrap: [AppComponent]
})
export class AppModule { }
