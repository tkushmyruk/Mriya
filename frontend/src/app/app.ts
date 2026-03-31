import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {HeaderComponent} from './components/header/header';
import {NavBar} from './nav-bar/nav-bar';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, HeaderComponent, NavBar],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('social-ui');
}
