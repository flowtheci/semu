import { NgModule, isDevMode } from '@angular/core';
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
import {MathjaxModule} from "mathjax-angular";
import { VoiceRecorderComponent } from './components/voice-recorder/voice-recorder.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import {ToastrModule} from "ngx-toastr";

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
    LogoComponent,
    VoiceRecorderComponent,
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    HttpClientModule,
    ToastrModule.forRoot(),
    MathjaxModule.forRoot({config: {
      tex: {
        inlineMath: [['$', '$'], ['\\(', '\\)']]
      },
      "HTML-CSS": {
        linebreaks: { automatic: true }
      },
      svg: {
        fontCache: 'global',
        linebreaks: { automatic: true }
      }
    }}),
    JwtModule.forRoot({
      config: {
        tokenGetter: tokenGetter,
        allowedDomains: ["semu-api.fly.dev", "localhost:8080", "tribal-saga-397814.lm.r.appspot.com"],
      },
    }),
  ],
  providers: [PromptUtil],
  bootstrap: [AppComponent]
})
export class AppModule { }
