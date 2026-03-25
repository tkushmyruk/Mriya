import {Component, OnInit} from '@angular/core';
import {ProfileService} from './service/profile-service';
import {ProfileModel} from './model/profile-model';
import {NavBar} from '../nav-bar/nav-bar';
import {RouterOutlet} from '@angular/router';
import {PostListComponent} from '../components/post-list/post-list';

@Component({
  selector: 'app-profile',
  imports: [
    NavBar,
    RouterOutlet,
    PostListComponent
  ],
  templateUrl: './profile.html',
  styleUrl: './profile.css',
})
export class ProfileComponent implements OnInit {

  public profile?: ProfileModel;

  constructor(
    private readonly profileService: ProfileService
  ) {}

  ngOnInit() {
    this.profileService.load().pipe().subscribe(profile =>
      this.profile = profile
    );
  }

  onFileSelected(event: any) {
    const file: File = event.target.files[0];

    if (file) {
      this.profileService.uploadAvatar(file).subscribe({
        next: (response) => {
          if (this.profile) {
            this.profile.avatarUrl = response.url;
          }
        },
        error: (err) => console.error('Error occured', err)
      });
    }
  }


}
