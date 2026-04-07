import {Component, OnInit, signal} from '@angular/core';
import {NavigationEnd, Router, RouterOutlet} from '@angular/router';
import {HeaderComponent} from './components/header/header';
import {NavBar} from './nav-bar/nav-bar';
import {filter} from 'rxjs';
import {NgIf} from '@angular/common';
import {AuthService} from './components/services/auth.service';
import {PresenceService} from './components/services/presense.service';
import {NotificationService} from './components/services/notification.service';
import {Notification} from './components/notification/notification';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, HeaderComponent, NavBar, NgIf, Notification],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit{

  showNavbar = true;

  protected readonly title = signal('social-ui');

  constructor(private router: Router,
              private authService: AuthService,
              private presenceService: PresenceService,
              private notificationService: NotificationService) {
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe((event: NavigationEnd) => {
      const excludedRoutes = ['/login'];
      this.showNavbar = !excludedRoutes.includes(event.urlAfterRedirects);
    });
  }

  ngOnInit() {
    if (this.authService.isLoggedIn()) {
      this.notificationService.connect();
      const userId = this.authService.getUserId();
      if (userId) {
        this.presenceService.startPresenceHeartbeat(userId).subscribe();
      }
    }
  }
}
