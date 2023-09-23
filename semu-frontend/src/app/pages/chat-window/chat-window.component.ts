import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {Message} from 'src/app/model/message';
import {SemuService} from "../../service/semu.service";


@Component({
  selector: 'app-chat-window',
  templateUrl: './chat-window.component.html',
  styleUrls: ['./chat-window.component.scss']
})
export class ChatWindowComponent implements OnInit {

  @ViewChild('messageBox') chatWindowElement!: HTMLElement;
  @Input() isOpen = false;

  messages: Message[] = [];
  messageIndex = 0;

  constructor(private semuService: SemuService) {


  }


  ngOnInit() {
    this.messages = [
      {
        id: 0,
        content: 'Hei! Mina olen SEMU, Sinu virtuaalne matemaatikaõpetaja. Kui Sul on mõni matemaatiline küsimus või probleem, siis olen siin, et Sind aidata. Koos saame kõigega hakkama! 😊',
        timestamp: new Date(),
        isUser: false,
        hasStartedTyping: false,
      },
      /*
      {
        id: 1,
        content: 'Tere! Mul on üks küsimus. Nimelt kuidas saaksin ma hshsd ak wdkl aufihad km gerhjiejg n awjkdb kaf nerjgh ksenmf kmbnseuif  knmskl nwjhd ad kaw ndawbnjk palun? Seleta nagu ma oleks viie aastane?',
        timestamp: new Date(),
        isUser: true,
        hasStartedTyping: false,
      },
      {
        id: 2,
        content: 'Muidugi! Proovin seletada nii lihtsalt kui võimalik. Kui sa mõtled sellele, siis...',
        timestamp: new Date(),
        isUser: false,
        hasStartedTyping: false,
      },
      {
        id: 3,
        content: 'Aitäh selgitamast! Aga kuidas on lood selle teise asjaga?',
        timestamp: new Date(),
        isUser: true,
        hasStartedTyping: false,
      },
      {
        id: 4,
        content: 'Hea küsimus! Selle teise asjaga on nii, et...',
        timestamp: new Date(),
        isUser: false,
        hasStartedTyping: false,
      },
      */
    ];

  }

  onTypingFinished(): void {
    if (this.messageIndex < this.messages.length - 1) {
      this.messageIndex++;
    }
  }

  async onSendMessage(message: string, elementRef: HTMLTextAreaElement): Promise<void> {
    this.messages.push({
      id: this.messages.length,
      content: message,
      timestamp: new Date(),
      isUser: true,
      hasStartedTyping: false,
    });
    this.messageIndex++;
    elementRef.value = '';

    const event = {target: elementRef};
    this.adjustTextareaHeight(event)

    const response: Message = await this.semuService.responseAsMessage(this.messages);
    console.warn(response)
    this.messages.push(response);
    this.messageIndex++;
    this.chatWindowElement.scrollTop = this.chatWindowElement.scrollHeight;
  }

  adjustTextareaHeight(event: any): void {
    const textarea = event.target;
    textarea.style.height = 'auto'; // Reset height to auto before calculating the scroll height
    textarea.style.height = textarea.scrollHeight + 'px';
  }





}


