import {Component, OnInit} from '@angular/core';
import {NotificationService} from '../services/notification.service';
import {NgIf} from '@angular/common';

@Component({
  selector: 'app-notification',
  imports: [
    NgIf
  ],
  templateUrl: './notification.html',
  styleUrl: './notification.css',
})
export class Notification implements OnInit {
  notification: any = null;

  constructor(private ns: NotificationService) {}

  ngOnInit() {
    this.ns.notification$.subscribe(data => {
      this.notification = data;
      setTimeout(() => this.close(), 5000);
    });
  }

  close() {
    this.notification = null;
  }
}
