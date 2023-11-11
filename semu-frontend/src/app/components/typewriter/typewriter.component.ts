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
  @Input() fast: boolean = false;
  @Output() typingFinished = new EventEmitter<boolean>();
  @ViewChild('textContainer') textContainerElement!: ElementRef;
  @ViewChild('blinkingCursor', { static: true }) blinkingCursorElement!: ElementRef;
  index = 0;
  typeSpeed = 50;
  fastTypeSpeed = 10;
  text = '';
  displayText = '';

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
    this.displayText = '';
    this.scrollOnType();
    this.type();
  }

  type(): void {
    if (this.index < this.text.length) {
      this.displayText = this.text.slice(0, this.index);
      this.index++;
      setTimeout(() => this.type(), Math.random() * (this.fast ? this.fastTypeSpeed : this.typeSpeed));
    } else {
      this.displayText = this.text.slice(0, this.index);
      this.typingFinished.emit(true); // Emit the typingFinished event when typing is finished
    }
  }

  checkIfTypedTextHasLatex(): boolean {
    const regex = /(\$\$.*?\$\$|\\\[.*?\\\]|\\\(.+?\\\)|\$[^$]*\$)/gs;
    return regex.test(this.text);
  }


  scrollOnType() {
    this.textContainerElement.nativeElement.scrollIntoView(true);
  }


  stopBlinking(): void {
    this.blinkingCursorElement.nativeElement.style.visibility = 'hidden';
  }

}
