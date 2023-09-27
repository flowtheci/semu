import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import { PromptUtil} from "../prompts";
import {Message} from "../model/message";

@Injectable({
  providedIn: 'root'
})
export class SemuService {
  private readonly textToSpeechUrl = 'https://api.tartunlp.ai/text-to-speech/v2';
  private readonly openaiUrl = 'https://api.openai.com/v1/chat/completions'; // Replace with the actual OpenAI API endpoint

  constructor(private http: HttpClient, private promptUtil: PromptUtil) { }

  get apiKey(): string {
    return localStorage.getItem('apiKey') || '';
  }

  textToSpeech(inputText: string): Observable<Blob> {
    const headers = {
      'Accept': 'audio/wav',
      'Content-Type': 'application/json',
    };
    const data = {
      text: inputText,
      speaker: 'tambet',
      speed: 1.3
    };
    return this.http.post(this.textToSpeechUrl, data, { headers, responseType: 'blob' });
  }

  async responseAsMessage(messages: Message[]): Promise<Message> {
    // Get the last message from the user
    const response = await this.aiResponse(messages);
    return {
      id: messages.length,
      content: response,
      timestamp: new Date(),
      isUser: false,
      hasStartedTyping: false,
    };
  }

  async aiResponse(prompt: Message[]): Promise<string> {
    // Call the OpenAI API and return the response
    const headers = {
      Authorization: 'Bearer ' + this.apiKey,
      'Content-Type': 'application/json',
    };

    let finalMessages = [];

    for (let i = 0; i < prompt.length; i++) {
      finalMessages.push({
        role: prompt[i].isUser ? 'user' : 'assistant',
        content: prompt[i].content,
      });
    }

    const data = {
      model: 'gpt-3.5-turbo',
      messages: [
        {
          role: 'system',
          content: this.promptUtil.getMathPrompt(localStorage.getItem('userClass') || 'none', localStorage.getItem('firstName') || 'none'),
        },
        ...finalMessages,
      ],
    };

    try {
      const response = this.http.post(this.openaiUrl, data, {headers});
      // @ts-ignore
      return (await response.toPromise()).choices[0].message.content;
    } catch (error) {
      console.error('Error calling OpenAI API:', error);
      throw error;
    }
  }
}
