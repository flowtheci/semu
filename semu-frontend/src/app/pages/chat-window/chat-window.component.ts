import {
  AfterViewInit,
  Component,
  ElementRef,
  Input,
  NgZone,
  OnChanges,
  OnInit,
  SimpleChanges,
  ViewChild
} from '@angular/core';
import {Message} from 'src/app/model/message';
import {SemuService} from "../../service/semu.service";
import {HttpClient} from "@angular/common/http";
import {backendUrl} from "../../app.component";
import {animate, state, style, transition, trigger} from '@angular/animations';
import * as RecordRTC from 'recordrtc';
import {AuthService} from "../../service/auth.service";


@Component({
  selector: 'app-chat-window',
  templateUrl: './chat-window.component.html',
  styleUrls: ['./chat-window.component.scss'],
  animations: [
    trigger('flyUpAndAway', [
      state('in', style({ opacity: 1, transform: 'translateY(0)' })),
      state('out', style({ opacity: 0, transform: 'translateY(-100%)' })),
      transition('* => out', [
        animate('0.5s ease-in')
      ])
    ]),
    trigger('flyUpAndAwayFar', [
      state('in', style({ opacity: 1, transform: 'translateY(0)' })),
      state('out', style({ opacity: 0, transform: 'translateY(-900%)' })),
      transition('* => out', [
        animate('0.5s ease-in')
      ])
    ]),
    trigger('flyChatBox', [
      state('out', style({ opacity: 0, transform: 'translateX(-50%) translateY(100%)' })),
      state('in', style({ opacity: 1, transform: 'translateX(-50%) translateY(0)' })),
      transition('* => in', [
        animate('0.5s ease-in')
      ])
    ]),
    trigger('inputFlyDown', [
      state('in', style({ opacity: 1, transform: 'translateY(0)' })),
      state('out', style({ opacity: 0, transform: 'translateY(200%)' })),
      transition('in => out', [
        animate('0.5s 0.5s ease-in-out')
      ]),
    ]),
  ]
})
export class ChatWindowComponent implements OnInit, AfterViewInit, OnChanges {

  @ViewChild('messageBox', {read: ElementRef}) chatWindowElement: ElementRef<any> | undefined;
  @ViewChild('bottomAnchor') bottomAnchor!: HTMLElement;
  @ViewChild('fileInput') fileInput!: ElementRef;
  @Input() isOpen = false;
  @Input() conversationIdToLoad: string = '';

  messages: Message[] = [];
  messageIndex = -1;
  isTyping = false;
  userIsTyping = false;
  isRecording = false;

  _rateLimited = false;

  conversationId: string = '0'
  conversationTitle: string = '';
  conversationArray: number[] = [];
  countdown: string = '';
  scrollTop = 0;

  private recordRTC: RecordRTC | undefined;
  private audioChunks: Blob[] = [];
  private pollInterval = 125;
  private maxInterval = 32000;
  private exponentialBackoff = 1.5;
  userLabel = '<span class="label">Sina\n</span>';
  assistantLabel = '<span class="label">SEMU\n</span>';

  animState = 'in';
  chatBoxAnimState = 'out';

  animate() {
    this.animState = 'out';
    this.chatBoxAnimState = 'in';
    // Return a promise that resolves after the timeout
    return new Promise<void>((resolve) => {
      setTimeout(() => {
        resolve(); // Resolve the promise after the timeout
      }, 500);
    });
  }


  constructor(
    private semuService: SemuService,
    private http: HttpClient,
    private zone: NgZone,
    private authService: AuthService,
    ) {
  }

  get inputboxAnimState() {
    return this._rateLimited ? 'out' : 'in';
  }

  get userName() {
    return localStorage.getItem('firstName') || '';
  }

  get hasConversationId(): boolean {
    return this.conversationId !== '0';
  }

  get showLeftArrow(): boolean {
    return this.conversationArray.indexOf(parseInt(this.conversationId)) > 0;
  }

  get showRightArrow(): boolean {
    return this.conversationArray.indexOf(parseInt(this.conversationId)) < this.conversationArray.length-1;
  }


  ngOnInit() {
    this.messages = [];
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['conversationIdToLoad']) {
      console.warn("NEW!!!!")
      this.conversationId = this.conversationIdToLoad;
      this.loadConversation();
    }
  }

  async ngAfterViewInit(): Promise<void> {
    await this.semuService.getAllConversations().then((response: any) => {
      this.conversationArray = response;
      this.conversationId = '0';
    });
    if (this.authService.getRateLimit() != '') {
      const countdownDate = new Date(this.authService.getRateLimit()).getTime();
      const now = new Date().getTime();
      const distance = countdownDate - now;
      if (distance > 0) {
        this._rateLimited = true;
        this.startCountdownLoop(countdownDate);
      } else {
        this._rateLimited = false;
        this.authService.deleteRateLimit();
      }
    }
  }

  startCountdownLoop(countdownTime: number) {
    const interval = setInterval(() => {
      const now = new Date().getTime();
      const distance = countdownTime - now;
      const hours = Math.floor((distance % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
      const minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
      const seconds = Math.floor((distance % (1000 * 60)) / 1000) + 1;
      this.countdown = (hours ? (hours +'h ') : '') + minutes + 'm ' + seconds + 's ';
      if (distance <= 0) {
        clearInterval(interval);
        this.countdown = '';
      }
    }, 1000);
  }


  isIOS(): boolean {
    return false;
  }

  async loadConversation(): Promise<void> {
    const conversation: Promise<object> = this.semuService.getConversationById(this.conversationId);
    conversation.then((response: any) => {
      this.chatBoxAnimState = 'in';
      this.conversationTitle = response.title;
      this.conversationId = response.conversationId;
      this.messages = response.messages.map((message: any) => {
        return {
          id: response.messages.indexOf(message),
          content: message.message,
          timestamp: new Date(message.timestamp),
          isUser: message.user,
          hasStartedTyping: false,
          isTypeable: false,
          fast: false,
        };
      });
      console.warn(this.messages);
      this.semuService.setLastConversationId(this.conversationId);
      this.messageIndex = this.messages.length-1;
      this.scroll();
    });
  }

  scroll() {
    this.scrollTop = (this.chatWindowElement?.nativeElement.scrollHeight - this.chatWindowElement?.nativeElement.clientHeight + 100) || 0;
    setTimeout(() => {
      this.scrollTop = (this.chatWindowElement?.nativeElement.scrollHeight - this.chatWindowElement?.nativeElement.clientHeight + 100) || 0;
    }, 100);
  }

  onTypingFinished(): void {
    if (this.messageIndex < this.messages.length - 1) {
      this.messageIndex++;
    } else {
      if (this.messages.length % 2 === 0) {
      }
    }
  }

  get isMobile(): boolean {
    return window.innerWidth <= 768;
  }

  async onSendMessage(message: string, elementRef?: HTMLTextAreaElement, isImage?: boolean): Promise<void> {
    if (elementRef) {
      elementRef.value = '';
    }
    await this.animate();
    const newId = this.messages.length;
    this.messages.push({
      id: newId,
      content: this.fixMessage(message),
      timestamp: new Date(),
      isUser: true,
      hasStartedTyping: false,
      isTypeable: false,
      isImage: isImage,
    });
    this.messageIndex++;
    this.isTyping = true;
    this.scroll();


    const response = this.semuService.responseAsMessage(this.messages, isImage);
    response.then((response: Message) => {
      this.isTyping = false;
      this.messages.push(response);
      this.messageIndex++;
      this.scroll();
      this.semuService.playLastAudio();
      if (this.conversationTitle === '') {
        this.conversationId = this.semuService.getLastConversationId();
        this.conversationTitle = this.semuService.getLastTitle();
      }
      if (!this.conversationArray.includes(parseInt(this.conversationId))) {
        this.conversationArray.push(parseInt(this.conversationId));
      }

      if (this.semuService._lastConversationReachedLimit) {
        this._rateLimited = true;
        this.authService.saveRateLimit(this.semuService._lastRateLimit);
        this.startCountdownLoop(new Date(this.authService.getRateLimit()).getTime());
      }
    });
  }

  fixMessage(message: string) {
    return message.replace(/\s+/g, ' ').trim();
  }


  convoExists(): boolean {
    return this.conversationArray.includes(parseInt(this.conversationId));
  }

  createNewConversation(): void {
      this.conversationId = '0';
      this.conversationTitle = '';
      this.messages = [];
      this.messageIndex = -1;
      this.animState = 'in';
      this.chatBoxAnimState = 'out';
  }

  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = this.handleReaderLoaded.bind(this);
      reader.readAsDataURL(file);
    }
  }

  handleReaderLoaded(e: { target: any; }) {
    const reader = e.target;
    const base64result = reader.result.substr(reader.result.indexOf(',') + 1);

    this.onSendMessage(base64result, undefined, true);
  }

  async startRecording() {
    this.isRecording = true;
    const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
    this.recordRTC = new RecordRTC(stream, { type: 'audio' });
    this.recordRTC.startRecording();
  }

  async stopRecording() {
    this.isRecording = false;
    this.recordRTC?.stopRecording(async () => {
      let blob = this.recordRTC?.getBlob();
      this.zone.run(() => {
        if (blob == undefined) return;
        this.sendAudioMessage(new File([blob], 'message.wav', { type: 'audio/wav' }));
      });
    });
  }

  toggleRecording() {
    this.isRecording ? this.stopRecording() : this.startRecording();
  }

  async sendAudioMessage(audioFile: File) {
    await this.animate();
    this.userIsTyping = true;
    const formData = new FormData();
    const newId = this.messages.length;
    const endpoint = this.hasConversationId ? '/addAudioMessage' : '/startAudioConversation';
    const conversationIdParam = this.hasConversationId ? `?conversationId=${this.conversationId}` : '';
    formData.append('audioMessage', audioFile);

    const response = this.http.post(backendUrl + 'conversations' + endpoint + conversationIdParam, formData);
    return response.toPromise().then(async (response: any) => {
      this.userIsTyping = false;
      this.messages.push({
        id: newId,
        content: response.content,
        timestamp: new Date(),
        isUser: true,
        hasStartedTyping: false,
        isTypeable: true,
        fast: false,
      });
      console.warn(this.messages);
      this.messageIndex++;
      this.isTyping = true;
      await this.getAudioResponse(response.conversationId);
    });
  }

  async getAudioResponse(id: string) {
    try {
      await this.http.get(backendUrl + 'conversations/getAudioResponse?conversationId=' + id).toPromise()
        .then((response: any) => {
          if (response.content) {
            const newId = this.messages[this.messages.length - 1].id + 1;
            this.messages.push({
              id: newId,
              content: response.content,
              timestamp: new Date(),
              isUser: false,
              hasStartedTyping: false,
              isTypeable: true,
              fast: false,
            });
            this.messageIndex++;
            this.pollInterval = 125;
            this.isTyping = false;
            if (response?.audio) {
              this.semuService.storeAudio(response.audio);
              this.semuService.playLastAudio();
            }
          } else {
            this.pollInterval *= this.exponentialBackoff;
            if (this.pollInterval > this.maxInterval) {
              console.error('Polling timed out, no response received. Last request response: ' + response);
            }
          }
        })
    } catch (error) {
      console.error('Error polling for response:', error);
    }
  }

  triggerFileInput() {
    this.fileInput.nativeElement.click();
  }





}


