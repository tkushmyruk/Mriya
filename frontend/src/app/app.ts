import { Component, signal } from '@angular/core';
import {NavigationEnd, Router, RouterOutlet} from '@angular/router';
import {HeaderComponent} from './components/header/header';
import {NavBar} from './nav-bar/nav-bar';
import {filter} from 'rxjs';
import {NgIf} from '@angular/common';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, HeaderComponent, NavBar, NgIf],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {

  showNavbar = true;

  protected readonly title = signal('social-ui');

  constructor(private router: Router) {
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe((event: NavigationEnd) => {
      const excludedRoutes = ['/login'];
      this.showNavbar = !excludedRoutes.includes(event.urlAfterRedirects);
    });
  }
}
