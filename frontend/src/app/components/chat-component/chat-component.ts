import { AfterViewChecked, Component, ElementRef, OnDestroy, OnInit, ViewChild, NgZone } from '@angular/core';
import { MessageDTO } from '../models/message-dto';
import { ChatService } from '../services/chat.service';
import { ActivatedRoute } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { FormsModule } from '@angular/forms';
import { DatePipe, NgClass, NgForOf, NgIf } from '@angular/common';
import { Subscription } from 'rxjs';
import {UserService} from '../services/user.service';
import {ProfileModel} from '../../profile/model/profile-model';

@Component({
  selector: 'app-chat-component',
  standalone: true,
  imports: [
    FormsModule,
    DatePipe,
    NgClass,
    NgForOf,
    NgIf
  ],
  templateUrl: './chat-component.html',
  styleUrl: './chat-component.css',
})
export class ChatComponent implements OnInit, OnDestroy, AfterViewChecked {
  @ViewChild('scrollContainer') private scrollContainer!: ElementRef;

  messages: MessageDTO[] = [];
  newMessage: string = '';
  targetUserId!: number;
  currentUserId!: number | null;
  targetUserProfile: ProfileModel | undefined;
  private messageSub!: Subscription;

  constructor(
    private route: ActivatedRoute,
    private chatService: ChatService,
    private authService: AuthService,
    private userService: UserService,
    private zone: NgZone
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.targetUserId = Number(params.get('id'));
      this.currentUserId = this.authService.getUserId();

      this.chatService.connect();
      this.loadTargetUserInfo();
      this.loadHistory();
    });

    this.messageSub = this.chatService.messages$.subscribe(msg => {
      this.zone.run(() => {
        const isRelevant = msg.senderId === this.targetUserId || msg.recipientId === this.targetUserId;
        const isDuplicate = this.messages.some(m => m.id === msg.id && msg.id !== null);

        if (isRelevant && !isDuplicate) {
          this.messages = [...this.messages, msg];
        }
      });
    });
  }

  private loadHistory(): void {
    this.chatService.getHistory(this.targetUserId).subscribe(history => {
      this.messages = history;
    });
  }

  send(): void {
    if (this.newMessage.trim() && this.currentUserId) {
      this.chatService.sendMessage(this.targetUserId,  this.currentUserId, this.newMessage);
      this.newMessage = '';
    }
  }

  ngAfterViewChecked() {
    this.scrollToBottom();
  }

  private scrollToBottom(): void {
    if (this.scrollContainer) {
      this.scrollContainer.nativeElement.scrollTop = this.scrollContainer.nativeElement.scrollHeight;
    }
  }

  private loadTargetUserInfo(): void {
    this.userService.getProfileByUserId(this.targetUserId).subscribe({
      next: (userProfile) => {
          this.targetUserProfile = userProfile;
      },
    });
  }

  ngOnDestroy(): void {
    if (this.messageSub) {
      this.messageSub.unsubscribe();
    }
    this.chatService.disconnect();
  }
}
