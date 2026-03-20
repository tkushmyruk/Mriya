import {Component, OnInit} from '@angular/core';
import {ProfileService} from './service/profile-service';
import {ProfileModel} from './model/profile-model';
import {NavBar} from '../nav-bar/nav-bar';
import {RouterOutlet} from '@angular/router';

@Component({
  selector: 'app-profile',
  imports: [
    NavBar,
    RouterOutlet
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


}
