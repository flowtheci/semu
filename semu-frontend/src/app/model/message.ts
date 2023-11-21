export interface Message {
  id: number;
  content: string;
  timestamp: Date;
  isUser: boolean;
  hasStartedTyping: boolean;
  isTypeable: boolean;
  fast?: boolean;
  isImage?: boolean;
  rateLimitReached?: boolean;
}
