import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LogInComponent } from './pages/log-in/log-in.component';
import { MainComponent } from './pages/main/main.component';
import { SideBarComponent } from './components/side-bar/side-bar.component';
import { ChatWindowComponent } from './pages/chat-window/chat-window.component';
import { TypewriterComponent } from './components/typewriter/typewriter.component';

@NgModule({
  declarations: [
    AppComponent,
    LogInComponent,
    MainComponent,
    SideBarComponent,
    ChatWindowComponent,
    TypewriterComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
