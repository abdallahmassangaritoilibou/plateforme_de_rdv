import { Component } from '@angular/core';
import { RouterLink, RouterOutlet, RouterLinkActive } from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  template: `
    <nav class="navbar">
      <div class="nav-brand">
        <h2>MyRDV.Lib</h2>
      </div>
      <div class="nav-links">
        <a routerLink="/login" 
           routerLinkActive="active" 
           class="nav-button">
          Connexion
        </a>
        <a routerLink="/register" 
           routerLinkActive="active" 
           class="nav-button">
          Inscription
        </a>
      </div>
    </nav>
    <div class="container">
      <router-outlet></router-outlet>
    </div>
  `,
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'interfaceRdv';
}