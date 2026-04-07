import { inject, Injectable, PLATFORM_ID } from '@angular/core';
import { AuthService } from './auth.service';
import * as Stomp from 'stompjs';
import { Subject } from 'rxjs';
import SockJS from 'sockjs-client';
import { isPlatformBrowser } from '@angular/common';

@Injectable({ providedIn: 'root' })
export class NotificationService {
  private stompClient: any;
  private audio: HTMLAudioElement | null = null;
  private platformId = inject(PLATFORM_ID);

  public notification$ = new Subject<any>();

  constructor(private authService: AuthService) {
    if (isPlatformBrowser(this.platformId)) {
      this.audio = new Audio('/notification123.mp3');
    }
  }

  connect() {
    if (!isPlatformBrowser(this.platformId)) return;

    const token = localStorage.getItem('access_token');
    if (!token) return;

    const socket = new SockJS('http://localhost:8080/ws');
    this.stompClient = Stomp.over(socket);

    const headers = {
      Authorization: `Bearer ${token}`
    };

    this.stompClient.connect(headers, (frame: any) => {

      this.stompClient.subscribe('/user/queue/notifications', (message: any) => {
        this.showNotification(JSON.parse(message.body));
      });
    }, (error: any) => {
      console.error('WS Error NOTIF: ', error);
    });
  }

  private showNotification(payload: any) {
    if (this.audio) {
      this.audio.play().catch(e => console.log('Sound play error NOTIF', e));
    }
    this.notification$.next(payload);
  }
}
