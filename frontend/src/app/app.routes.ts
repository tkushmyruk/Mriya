import {Routes} from '@angular/router';
import {ProfileComponent} from './profile/profile';
import {FriendsComponent} from './friends/friends';

export const routes: Routes = [
  {
    path: 'profile', component: ProfileComponent,
  },
  { path: 'friends', component: FriendsComponent }
];
