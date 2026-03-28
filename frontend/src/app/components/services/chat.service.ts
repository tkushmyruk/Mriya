import { Injectable } from '@angular/core';
import { Client } from '@stomp/stompjs';
import {Observable, Subject} from 'rxjs';
import {MessageDTO} from '../models/message-dto';
import {HttpClient} from '@angular/common/http';
import SockJS from 'sockjs-client';
import {ChatSummaryDTO} from '../models/chat-summary-dto';

@Injectable({ providedIn: 'root' })
export class ChatService {
  private stompClient: Client | null = null;
  public messageSource = new Subject<MessageDTO>();
  public messages$ = this.messageSource.asObservable();

  private apiUrl = 'http://localhost:8080/api/messages';


  constructor(private http: HttpClient) {}

  connect() {
    const token = localStorage.getItem('access_token');
    const socket = new SockJS('http://localhost:8080/ws');

    this.stompClient = new Client({
      webSocketFactory: () => socket,
      connectHeaders: {
        'Authorization': `Bearer ${token}`
      },
      debug: (str) => console.log(str),
      onConnect: (frame) => {
        this.stompClient?.subscribe('/user/queue/messages', (message) => {
          this.messageSource.next(JSON.parse(message.body));
        });
      }
    });
    this.stompClient.activate();
  }

  sendMessage(recipientId: number, currentUserId: number | null, content: string) {
    if (this.stompClient && this.stompClient.connected) {
      const msg = {
        recipientId: recipientId,
        content: content,
        senderId: currentUserId
      };

      this.stompClient.publish({
        destination: '/app/chat.send',
        body: JSON.stringify(msg)
      });
    }
  }

  disconnect() {
    this.stompClient?.deactivate();
  }



  getHistory(recipientId: number): Observable<MessageDTO[]> {
    return this.http.get<MessageDTO[]>(`${this.apiUrl}/history/${recipientId}`);
  }

  getChatList(): Observable<ChatSummaryDTO[]> {
    return this.http.get<ChatSummaryDTO[]>(`${this.apiUrl}/chats`);
  }
}
