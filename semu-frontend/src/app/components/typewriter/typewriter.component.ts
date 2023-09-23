import {
  Component,
  ElementRef,
  EventEmitter,
  Input,
  OnChanges,
  OnInit,
  Output,
  SimpleChanges,
  ViewChild
} from '@angular/core';
import {Message} from "../../model/message";

@Component({
  selector: 'app-typewriter',
  templateUrl: './typewriter.component.html',
  styleUrls: ['./typewriter.component.scss']
})
export class TypewriterComponent implements OnInit, OnChanges {
  @Input() message: Message | undefined;
  @Input() startTyping: boolean = false;
  @Output() typingFinished = new EventEmitter<boolean>();
  @ViewChild('textContainer') textContainerElement!: ElementRef;
  @ViewChild('blinkingCursor', { static: true }) blinkingCursorElement!: ElementRef;
  index = 0;
  text = '';

  ngOnInit(): void {
    this.text = this.message?.content || ''
    this.blinkingCursorElement.nativeElement.style.visibility = 'hidden';
    this.typingFinished.subscribe(() => {
      setTimeout(() => this.stopBlinking(), 1500);
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (this.startTyping && !this.message?.hasStartedTyping) {
      setTimeout(() => {
        this.message!.hasStartedTyping = true;
        this.startType();
      });
    }
  }


  startType(): void {
    console.warn('startType : ' + this.text);
    this.blinkingCursorElement.nativeElement.style.visibility = 'visible';
    this.textContainerElement.nativeElement.textContent = '';
    this.type();
  }

  type(): void {
    if (this.index < this.text.length) {
      this.textContainerElement.nativeElement.textContent = this.text.slice(0, this.index);
      this.index++;
      setTimeout(() => this.type(), Math.random() * 40 + 20);
    } else {
      this.textContainerElement.nativeElement.textContent = this.text.slice(0, this.index);
      this.typingFinished.emit(true); // Emit the typingFinished event when typing is finished
    }
  }


  stopBlinking(): void {
    this.blinkingCursorElement.nativeElement.style.visibility = 'hidden';
  }

}
