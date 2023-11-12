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


@Component({
  selector: 'app-chat-window',
  templateUrl: './chat-window.component.html',
  styleUrls: ['./chat-window.component.scss']
})
export class ChatWindowComponent implements OnInit, AfterViewInit, OnChanges {

  @ViewChild('messageBox') chatWindowElement!: HTMLElement;
  @ViewChild('typingNotification') typingNotification!: HTMLElement;
  @ViewChild('fileInput') fileInput!: ElementRef;
  @Input() isOpen = false;
  @Input() conversationIdToLoad: string = '';

  messages: Message[] = [];
  messageIndex = 0;
  isTyping = false;
  userIsTyping = false;
  isRecording = false;

  conversationId: string = '0'
  conversationTitle: string = '';
  conversationArray: number[] = [];

  private mediaRecorder: MediaRecorder | null = null;
  private audioChunks: Blob[] = [];
  private pollInterval = 125;
  private maxInterval = 32000;
  private exponentialBackoff = 1.5;

  constructor(private semuService: SemuService, private http: HttpClient, private zone: NgZone) {
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
    this.messages = [
      {
        id: 0,
        content: 'Hei semu! Mina olen SEMU, Sinu virtuaalne matemaatikaõpetaja. Kui Sul on mõni matemaatiline küsimus või probleem, siis olen siin, et Sind aidata. Koos saame kõigega hakkama! 😊',
        timestamp: new Date(),
        isUser: false,
        hasStartedTyping: false,
        isTypeable: true,
      },
    ];
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
      this.conversationId = this.conversationArray[this.conversationArray.length-1].toString();
      this.loadConversation();
    });

  }

  async loadConversation(): Promise<void> {
    const conversation: Promise<object> = this.semuService.getConversationById(this.conversationId);
    conversation.then((response: any) => {
      this.conversationTitle = response.title;
      this.conversationId = response.conversationId;
      this.messages = response.messages.map((message: any) => {
        return {
          id: response.messages.indexOf(message),
          content: message.message,
          timestamp: new Date(message.timestamp),
          isUser: message.user,
          hasStartedTyping: false,
          isTypeable: response.messages.indexOf(message) === response.messages.length - 1,
          fast: response.messages.indexOf(message) === response.messages.length - 1,
        };
      });
      console.warn(this.messages);
      this.semuService.setLastConversationId(this.conversationId);
      this.messageIndex = this.messages.length-1;
    });
  }

  onTypingFinished(): void {
    if (this.messageIndex < this.messages.length - 1) {
      this.messageIndex++;
    } else {
      if (this.messages.length % 2 === 0) {
        this.typingNotification.scrollIntoView(false);
      }
    }
  }

  async onSendMessage(message: string, elementRef?: HTMLTextAreaElement, isImage?: boolean): Promise<void> {
    const newId = this.messages.length;
    this.messages.push({
      id: newId,
      content: this.fixMessage(message),
      timestamp: new Date(),
      isUser: true,
      hasStartedTyping: false,
      isTypeable: false,
    });
    this.messageIndex++;
    this.isTyping = true;

    if (elementRef) {
      elementRef.value = '';
      const event = {target: elementRef};
      this.adjustTextareaHeight(event)
    }

    const response = this.semuService.responseAsMessage(this.messages, isImage);
    response.then((response: Message) => {
      this.isTyping = false;
      this.messages.push(response);
      this.messageIndex++;
      this.semuService.playLastAudio();
      if (this.conversationTitle === '') {
        this.conversationId = this.semuService.getLastConversationId();
        this.conversationTitle = this.semuService.getLastTitle();
      }
      if (!this.conversationArray.includes(parseInt(this.conversationId))) {
        this.conversationArray.push(parseInt(this.conversationId));
      }
    });
  }

  fixMessage(message: string) {
    return message.replace(/\s+/g, ' ').trim();
  }

  adjustTextareaHeight(event: any): void {
    const textarea = event.target;
    textarea.style.height = 'auto'; // Reset height to auto before calculating the scroll height
    textarea.style.height = textarea.scrollHeight + 'px';
  }


  convoExists(): boolean {
    return this.conversationArray.includes(parseInt(this.conversationId));
  }

  createNewConversation(): void {
      this.conversationId = '0';
      this.conversationTitle = '';
      this.messages = [
        {
          id: 0,
          content: 'Hei ' + this.userName + '! Mina olen SEMU, Sinu virtuaalne matemaatikaõpetaja. Kui Sul on mõni matemaatiline küsimus või probleem, siis olen siin, et Sind aidata. Koos saame kõigega hakkama! 😊',
          timestamp: new Date(),
          isUser: false,
          hasStartedTyping: false,
          isTypeable: true,
        },
      ];
      this.messageIndex = 0;
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
    this.mediaRecorder = new MediaRecorder(stream);
    this.mediaRecorder.ondataavailable = e => {
      this.audioChunks.push(e.data);
    };
    this.mediaRecorder.start();
  }

  stopRecording() {
    this.isRecording = false;
    if (!this.mediaRecorder) return;
    this.mediaRecorder.stop();
    this.mediaRecorder.onstop = async () => {
      const audioBlob = new Blob(this.audioChunks, { type: 'audio/wav' });
      this.audioChunks = [];
      const audioFile = new File([audioBlob], 'message.wav', { type: 'audio/wav' });
      this.isRecording = false;
      console.warn(audioFile);
      this.zone.run(() => {
        this.sendAudioMessage(audioFile);
      });
    };
  }

  toggleRecording() {
    this.isRecording ? this.stopRecording() : this.startRecording();
  }

  async sendAudioMessage(audioFile: File) {
    this.userIsTyping = true;
    const formData = new FormData();
    const newId = this.messages[this.messages.length - 1].id + 1;
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


