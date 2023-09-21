import {Component, Input, OnInit} from '@angular/core';
import {Message} from 'src/app/model/message';


@Component({
  selector: 'app-chat-window',
  templateUrl: './chat-window.component.html',
  styleUrls: ['./chat-window.component.scss']
})
export class ChatWindowComponent implements OnInit {

  @Input() isOpen = false;

  messages: Message[] | undefined;

  ngOnInit() {
    this.messages = [
      {
        id: 1,
        content: 'Hei! Mina olen SEMU, Sinu virtuaalne matemaatikaõpetaja. Kui Sul on mõni matemaatiline küsimus või probleem, siis olen siin, et Sind aidata. Koos saame kõigega hakkama! 😊',
        timestamp: new Date(),
        isUser: false
      },
      {
        id: 2,
        content: 'Tere! Mul on üks küsimus. Nimelt kuidas saaksin ma hshsd ak wdkl aufihad km gerhjiejg n awjkdb kaf nerjgh ksenmf kmbnseuif  knmskl nwjhd ad kaw ndawbnjk palun? Seleta nagu ma oleks viie aastane?',
        timestamp: new Date(),
        isUser: true
      },
      {
        id: 3,
        content: 'Muidugi! Proovin seletada nii lihtsalt kui võimalik. Kui sa mõtled sellele, siis...',
        timestamp: new Date(),
        isUser: false
      },
      {
        id: 4,
        content: 'Aitäh selgitamast! Aga kuidas on lood selle teise asjaga?',
        timestamp: new Date(),
        isUser: true
      },
      {
        id: 5,
        content: 'Hea küsimus! Selle teise asjaga on nii, et...',
        timestamp: new Date(),
        isUser: false
      },
    ];

  }


}


