import { Component } from '@angular/core';
import {NavBar} from '../nav-bar/nav-bar';
import {RouterOutlet} from '@angular/router';

@Component({
  selector: 'app-friends',
  imports: [
    NavBar,
    RouterOutlet
  ],
  templateUrl: './friends.html',
  styleUrl: './friends.css',
})
export class FriendsComponent {

}
