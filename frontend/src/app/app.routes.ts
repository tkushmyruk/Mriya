import {Routes} from '@angular/router';
import {ProfileComponent} from './profile/profile';
import {FriendsComponent} from './friends/friends';
import {LoginComponent} from './components/auth/login/login';

export const routes: Routes = [
  {
    path: 'profile', component: ProfileComponent,
  },
  { path: 'friends', component: FriendsComponent },
  { path: 'login', component: LoginComponent }
];
