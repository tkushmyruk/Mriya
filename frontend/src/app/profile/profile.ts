import {Component, OnInit} from '@angular/core';
import {ProfileService} from './service/profile-service';
import {ProfileModel} from './model/profile-model';
import {NavBar} from '../nav-bar/nav-bar';
import {RouterOutlet} from '@angular/router';
import {PostListComponent} from '../components/post-list/post-list';
import {AuthService} from '../components/services/auth.service';
import {NgIf} from '@angular/common';

@Component({
  selector: 'app-profile',
  imports: [
    NavBar,
    RouterOutlet,
    PostListComponent,
    NgIf
  ],
  templateUrl: './profile.html',
  styleUrl: './profile.css',
})
export class ProfileComponent implements OnInit {

  public profile?: ProfileModel;
  public isOwner: boolean = false;
  public userId: number | null = 0;

  constructor(
    private readonly profileService: ProfileService,
    private authService: AuthService
  ) {}

  ngOnInit() {
    this.userId = this.authService.getUserId();
    console.log("OnInit: Retrieved userId", this.userId);
    if (this.userId != null) {
      this.profileService.load(this.userId).pipe().subscribe(profile => {
        this.profile = profile;
        console.log(this.profile.profilePhoto)
        this.isOwner = (profile.userId === this.userId);
      });
    }
  }

  get currentAvatar(): string {
    return this.profile?.profilePhoto ? this.profile.profilePhoto : 'avatar-default.png';
  }

  onFileSelected(event: any) {
    const file: File = event.target.files[0];

    if (file) {
      if (this.userId != null) {
        this.profileService.uploadAvatar(file, this.userId).subscribe({
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


}
