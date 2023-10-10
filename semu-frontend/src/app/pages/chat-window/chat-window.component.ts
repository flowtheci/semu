import {AfterViewInit, Component, Input, OnChanges, OnInit, SimpleChanges, ViewChild} from '@angular/core';
import {Message} from 'src/app/model/message';
import {SemuService} from "../../service/semu.service";


@Component({
  selector: 'app-chat-window',
  templateUrl: './chat-window.component.html',
  styleUrls: ['./chat-window.component.scss']
})
export class ChatWindowComponent implements OnInit, AfterViewInit, OnChanges {

  @ViewChild('messageBox') chatWindowElement!: HTMLElement;
  @ViewChild('typingNotification') typingNotification!: HTMLElement;
  @Input() isOpen = false;
  @Input() conversationIdToLoad: string = '';

  messages: Message[] = [];
  messageIndex = 0;
  isTyping = false;

  conversationId: string = '0'
  conversationTitle: string = '';
  conversationArray: number[] = [];

  constructor(private semuService: SemuService) {
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
        content: 'Hei ' + this.userName + '! Mina olen SEMU, Sinu virtuaalne matemaatikaõpetaja. Kui Sul on mõni matemaatiline küsimus või probleem, siis olen siin, et Sind aidata. Koos saame kõigega hakkama! 😊',
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
        this.isTyping = true;
        this.typingNotification.scrollIntoView(false);
      }
    }
  }

  async onSendMessage(message: string, elementRef: HTMLTextAreaElement): Promise<void> {
    const newId = this.messages.length;
    this.messages.push({
      id: newId,
      content: this.fixMessage(message),
      timestamp: new Date(),
      isUser: true,
      hasStartedTyping: false,
      isTypeable: true,
    });
    this.messageIndex++;
    elementRef.value = '';

    const event = {target: elementRef};
    this.adjustTextareaHeight(event)

    const response = this.semuService.responseAsMessage(this.messages);
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
}


