import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import { PromptUtil} from "../prompts";
import {Message} from "../model/message";
import {AuthService} from "./auth.service";
import {environment} from "../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class SemuService {

  _lastConversationId: string = '';
  _lastTitle: string = '';
  _lastConversationReachedLimit = false;
  _lastRateLimit = '';
  _lastAudioUrl: string = '';
  _loading: boolean = false;

  _selectedConversation = '';



  constructor(private http: HttpClient, private promptUtil: PromptUtil, private authService: AuthService) { }

  get apiKey(): string {
    return localStorage.getItem('apiKey') || '';
  }

  get selectedConversation(): string {
    return this._selectedConversation;
  }

  set selectedConversation(id: string) {
    this._selectedConversation = id;


  }

  shouldInstantlyType(): boolean {
    return this._loading;
  }


  async responseAsMessage(messages: Message[], isImage?: boolean): Promise<Message> {
    try {// Get the last message from the user
      return this.aiResponse(messages, isImage).then(
        (response: string) => {
          return {
            id: messages[messages.length - 1].id + 1,
            content: response,
            timestamp: new Date(),
            isUser: false,
            hasStartedTyping: false,
            isTypeable: true,
          };
        }
      );
    } catch (e) {
      console.error('Error calling backend API:', e);
      throw e;
    }
  }

  async audioResponse() {

  }

  getLastConversationId(): string {
    return this._lastConversationId;
  }

  setLastConversationId(id: string): void {
    this._lastConversationId = id;
  }

  getLastTitle(): string {
    return this._lastTitle;
  }

  setLastTitle(title: string): void {
    this._lastTitle = title;
  }

  getAllConversations(): Promise<number[]> {
    this._loading = true;
    try {
      const headers = {
        'Content-Type': 'application/json',
      };

      const response = this.http.get(environment.apiUrl + 'conversations/getAllUserConversations', {headers});
      return response.toPromise().then((response: any) => {
        return response;
      });

    }
    catch (e) {
      console.error('Error calling backend API:', e);
      throw e;
    }
  }

  getConversationTitles(amount: number): Promise<object> {
    try {
      const headers = {
        'Content-Type': 'application/json',
      };

      const response = this.http.get(environment.apiUrl + 'conversations/getLastConversationTitles?amount=' + amount, {headers});
      return response.toPromise().then((response: any) => {
        return response;
      });

    }
    catch (e) {
      console.error('Error calling backend API:', e);
      throw e;
    }
  }

  getConversationById(id: string): Promise<object> {
    try {
      const headers = {
        'Content-Type': 'application/json',
      };

      const response = this.http.get(environment.apiUrl + 'conversations/getConversation?conversationId=' + id, {headers});
      return response.toPromise().then((response: any) => {
        console.error(response);
        setTimeout(
          () => {
            this._loading = false;
          }
        , 125
        )
        return response;
      });

    }
    catch (e) {
      console.error('Error calling backend API:', e);
      throw e;
    }
  }

  playLastAudio() {
    const audio = new Audio(this._lastAudioUrl);
    audio.play();
  }

  storeAudio(audio: string) {
    const byteCharacters = atob(audio);
    const byteNumbers = Array.from(byteCharacters).map(char => char.charCodeAt(0));
    const byteArray = new Uint8Array(byteNumbers);
    const audioBlob = new Blob([byteArray], { type: 'audio/wav' });
    this._lastAudioUrl = URL.createObjectURL(audioBlob);
  }

  async aiResponse(prompt: Message[], isImage?: boolean): Promise<string> {
    // Call the backend and return response
    const headers = {
      'Content-Type': 'application/json',
    };

    let finalMessages = [];
    for (let i = 0; i < prompt.length; i++) {
      finalMessages.push(prompt[i].content);
    }

    const lastMessage = prompt[prompt.length - 1].content;

    const isFirstMessage = prompt.length === 1;
    try {
      const response = isFirstMessage
        ? this.http.post(environment.apiUrl + 'conversations/start' + (isImage ? 'Image' : '') + 'Conversation',
          isImage ? lastMessage : finalMessages, {headers})
        : this.http.post(environment.apiUrl + 'conversations/add' + (isImage ? 'Image' : '') + 'Message?conversationId=' + this.getLastConversationId(),
          finalMessages[finalMessages.length-1], {headers});

      return response.toPromise().then((response: any) => {
        this._lastConversationId = response.id;
        this._lastTitle = response.title;
        this._lastConversationReachedLimit = false;
        if (response?.audio) {
          this.storeAudio(response.audio);
        } else {
          this._lastAudioUrl = '';
        }
        return response.lastMessage;
      },
        (error) => {
          if (error.status === 423) {
            console.warn('User rate limited');
            console.warn(error);
            this._lastConversationId = error.error.id;
            this._lastConversationReachedLimit = true;
            this._lastRateLimit = error.error.lastMessageTimestamp;
            return error.error.lastMessage;
          }
        });

    } catch (error) {
      console.error('Error calling backend API:', error);
      throw error;
    }
  }
}
