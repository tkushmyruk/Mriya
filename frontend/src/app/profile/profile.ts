import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {ProfileService} from './service/profile-service';
import {ProfileModel} from './model/profile-model';
import {NavBar} from '../nav-bar/nav-bar';
import {ActivatedRoute, RouterLink, RouterOutlet} from '@angular/router';
import {PostListComponent} from '../components/post-list/post-list';
import {AuthService} from '../components/services/auth.service';
import {NgIf} from '@angular/common';

@Component({
  selector: 'app-profile',
  imports: [
    NavBar,
    RouterOutlet,
    PostListComponent,
    NgIf,
    RouterLink
  ],
  templateUrl: './profile.html',
  styleUrl: './profile.css',
})
export class ProfileComponent implements OnInit {

  public profile?: ProfileModel;
  public isOwner: boolean = false;
  public userId: number | null = 0;
  public friendshipStatus: string = 'NONE';
  @ViewChild('avatarWrapper') avatarWrapper!: ElementRef;

  constructor(
    private route: ActivatedRoute,
    private readonly profileService: ProfileService,
    private authService: AuthService
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
    return this.profile?.profilePhoto ? this.profile.profilePhoto : 'avatar-default.png';
  }

  onFileSelected(event: any) {
    const file: File = event.target.files[0];
    if (file) {
      if (this.userId != null) {
        this.profileService.uploadAvatar(file).subscribe({
          next: (response) => {
            if (this.profile) {
              this.profile.profilePhoto = response.url;
            }
          },
          error: (err) => console.error('Error occured', err)
        });
      }
    }
  }

  private loadMyProfile() {
    this.profileService.loadMyProfile().subscribe(p => {
      this.profile = p;
      this.isOwner = true;
    });
  }

  private loadUserProfile(id: number) {
    this.profileService.loadPublicProfile(id).subscribe(p => {
      this.profile = p;
      this.isOwner = (p.id === this.authService.getUserId());
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

}
