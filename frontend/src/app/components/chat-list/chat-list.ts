import { Component, OnInit } from '@angular/core';
import {ChatSummaryDTO} from '../models/chat-summary-dto';
import {ChatService} from '../services/chat.service';
import {DatePipe, NgForOf, NgIf} from '@angular/common';
import {RouterLink} from '@angular/router';

@Component({
  selector: 'app-chat-list',
  templateUrl: './chat-list.html',
  imports: [
    DatePipe,
    RouterLink,
    NgForOf,
    NgIf
  ],
  styleUrls: ['./chat-list.css']
})
export class ChatListComponent implements OnInit {
  chats: ChatSummaryDTO[] = [];

  constructor(private chatService: ChatService) {}

  ngOnInit(): void {
    this.chatService.getChatList().subscribe(data => {
      this.chats = data;
    });
  }
}
