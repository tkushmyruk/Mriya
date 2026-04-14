import {Component, ElementRef, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {ProfileService} from './service/profile-service';
import {ProfileModel} from './model/profile-model';
import {ActivatedRoute, RouterLink} from '@angular/router';
import {PostListComponent} from '../components/post-list/post-list';
import {AuthService} from '../components/services/auth.service';
import {NgIf} from '@angular/common';
import {PresenceService} from '../components/services/presense.service';
import {Subscription} from 'rxjs';

@Component({
  selector: 'app-profile',
  imports: [
    PostListComponent,
    NgIf,
    RouterLink
  ],
  templateUrl: './profile.html',
  styleUrl: './profile.css',
})
export class ProfileComponent implements OnInit, OnDestroy {

  public profile?: ProfileModel;
  public isOwner: boolean = false;
  public userId: number | null = 0;
  public friendshipStatus: string = 'NONE';
  isOnline: boolean = false;

  private presenceSubscription?: Subscription;

  @ViewChild('avatarWrapper') avatarWrapper!: ElementRef;

  constructor(
    private route: ActivatedRoute,
    private readonly profileService: ProfileService,
    private authService: AuthService,
    private presenceService: PresenceService
  ) {}

  ngOnInit() {
    this.route.params.subscribe(params => {
      const id = params['id'];

      if (id === 'me') {
        this.loadMyProfile();
        this.friendshipStatus = 'SELF';
      } else {
        const targetId = Number(id);
        this.loadUserProfile(targetId);
        this.checkFriendship(targetId);
      }
    });
  }

  get currentAvatar(): string {
    if (this.profile && this.profile.profilePhoto && this.profile.profilePhoto.trim() !== '') {
      return this.profile.profilePhoto;
    }
    return 'avatar-default.png';
  }

  onFileSelected(event: any) {
    const file: File = event.target.files[0];
    if (!file) return;

    const reader = new FileReader();
    reader.onload = (e: any) => {
      if (this.profile) {
        this.profile = { ...this.profile, profilePhoto: e.target.result };
      }
    };
    reader.readAsDataURL(file);

    this.profileService.uploadAvatar(file).subscribe({
      next: (response) => {
        if (this.profile && response.url) {
          const finalUrl = response.url.includes('?')
            ? `${response.url}&t=${Date.now()}`
            : `${response.url}?t=${Date.now()}`;

          this.profile = { ...this.profile, profilePhoto: finalUrl };
          console.log("Photo updated to:", finalUrl);
        }
      },
      error: (err) => {
        console.error('Upload failed', err);
        this.loadMyProfile();
      }
    });
  }

  private loadMyProfile() {
    this.profileService.loadMyProfile().subscribe(p => {
      this.profile = p;
      this.isOwner = true;
      this.loadUserPresence();
    });
  }

  private loadUserProfile(id: number) {
    console.log("LOAD")
    this.profileService.loadPublicProfile(id).subscribe(p => {
      this.profile = p;
      console.log(p)
      this.isOwner = (p.userId === this.authService.getUserId());
      this.loadUserPresence();
    });
  }

  loadUserPresence() {
    if (!this.profile || !this.profile.userId) {
      console.warn("Profile or userId is missing");
      return;
    }

    this.presenceSubscription?.unsubscribe();

    console.log("loaduserP " + this.profile.userId)
    this.presenceSubscription = this.presenceService
      .getLiveStatus(this.profile.userId)
      .subscribe(online => {
        this.isOnline = online;
      });
  }

  private checkFriendship(id: number | null) {
    this.profileService.getFriendshipStatus(id).subscribe({
      next: (status) => this.friendshipStatus = status,
      error: (err) => console.error('Помилка статусу друзів', err)
    });
  }

  public addFriend() {
    if (this.profile?.id) {
      this.profileService.sendFriendRequest(this.profile.id).subscribe({
        next: () => {
          this.friendshipStatus = 'PENDING';
        },
        error: (err) => alert('Не вдалося надіслати запит')
      });
    }
  }

  onMouseMove(event: MouseEvent) {
    if (!this.avatarWrapper) return;
    const rect = this.avatarWrapper.nativeElement.getBoundingClientRect();
    const x = event.clientX - rect.left;
    const y = event.clientY - rect.top;
    const xPercent = Math.round((x / rect.width) * 100);
    const yPercent = Math.round((y / rect.height) * 100);
    this.avatarWrapper.nativeElement.style.setProperty('--mouse-x', `${xPercent}%`);
    this.avatarWrapper.nativeElement.style.setProperty('--mouse-y', `${yPercent}%`);
  }

  ngOnDestroy() {
    this.presenceSubscription?.unsubscribe();
  }
}
