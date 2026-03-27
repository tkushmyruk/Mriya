// friends.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import {ProfileService} from '../profile/service/profile-service';
import {RouterLink, RouterOutlet} from '@angular/router';
import {NavBar} from '../nav-bar/nav-bar';
import {UserModel} from '../components/models/user.model';

@Component({
  selector: 'app-friends',
  standalone: true,
  imports: [CommonModule, RouterOutlet, NavBar, RouterLink],
  templateUrl: './friends.html',
  styleUrls: ['./friends.css']
})
export class FriendsComponent implements OnInit {
  friends: UserModel[] = [];
  requests: UserModel[] = [];
  activeTab: 'all' | 'requests' = 'all';

  constructor(private profileService: ProfileService) {}

  ngOnInit() {
    this.loadAll();
  }

  loadAll() {
    this.profileService.getFriendsList().subscribe(data => this.friends = data);
    this.profileService.getIncomingRequests().subscribe(data => this.requests = data);
  }

  acceptFriend(id: number) {
    this.profileService.acceptFriendRequest(id).subscribe(() => {
      this.loadAll();
    });
  }

  declineFriend(id: number) {
    this.profileService.declineFriendRequest(id).subscribe(() => {
      this.loadAll();
    });
  }
}
