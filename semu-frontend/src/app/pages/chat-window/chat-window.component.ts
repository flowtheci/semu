import {AfterViewInit, Component, Input, OnInit, ViewChild} from '@angular/core';
import {Message} from 'src/app/model/message';
import {SemuService} from "../../service/semu.service";


@Component({
  selector: 'app-chat-window',
  templateUrl: './chat-window.component.html',
  styleUrls: ['./chat-window.component.scss']
})
export class ChatWindowComponent implements OnInit, AfterViewInit {

  @ViewChild('messageBox') chatWindowElement!: HTMLElement;
  @Input() isOpen = false;

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
          isTypeable: false,
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
      }
    }
  }

  async onSendMessage(message: string, elementRef: HTMLTextAreaElement): Promise<void> {
    const newId = this.messages.length;
    this.messages.push({
      id: newId,
      content: message,
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
      if (this.conversationTitle === '') {
        this.conversationId = this.semuService.getLastConversationId();
        this.conversationTitle = this.semuService.getLastTitle();
      }
      if (!this.conversationArray.includes(parseInt(this.conversationId))) {
        this.conversationArray.push(parseInt(this.conversationId));
      }
    });
  }

  adjustTextareaHeight(event: any): void {
    const textarea = event.target;
    textarea.style.height = 'auto'; // Reset height to auto before calculating the scroll height
    textarea.style.height = textarea.scrollHeight + 'px';
  }

  loadPreviousConversation(): void {
    this.conversationId = this.conversationArray[this.conversationArray.indexOf(parseInt(this.conversationId))-1].toString();
    if (this.convoExists()) {
      this.loadConversation();
    } else {
      this.createNewConversation();
    }
  }

  loadNextConversation(): void {
    this.conversationId = this.conversationArray[this.conversationArray.indexOf(parseInt(this.conversationId))+1].toString();
    if (this.convoExists()) {
      this.loadConversation();
    } else {
      this.createNewConversation();
    }
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


