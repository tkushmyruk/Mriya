import { Routes } from '@angular/router';
import { ProfileComponent } from './profile/profile';
import { FriendsComponent } from './friends/friends';
import { LoginComponent } from './components/auth/login/login';
import { SearchComponent } from './components/search-component/search-component';
import { ChatListComponent } from './components/chat-list/chat-list';
import {ChatComponent} from './components/chat-component/chat-component';
import {FeedComponent} from './components/feed/feed';

export const routes: Routes = [
  { path: 'profile', component: ProfileComponent },
  { path: 'profile/:id', component: ProfileComponent },
  { path: '', redirectTo: '/feed', pathMatch: 'full' },
  { path: 'feed', component: FeedComponent },
  { path: 'friends', component: FriendsComponent },
  { path: 'login', component: LoginComponent },
  { path: 'search', component: SearchComponent },
  { path: 'chats', component: ChatListComponent },
  { path: 'chat/:id', component: ChatComponent },
  { path: '', redirectTo: 'profile', pathMatch: 'full' }
];
