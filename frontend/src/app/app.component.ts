import { Component } from '@angular/core';
import {
  Router,
  RouterLink,
  RouterLinkActive,
  RouterOutlet,
} from '@angular/router';
import { IonIcon } from '@ionic/angular';
import { addIcons } from 'ionicons';
import {
  addOutline,
  arrowBackOutline,
  calendarOutline,
  homeOutline,
  personCircleOutline,
} from 'ionicons/icons';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, RouterLinkActive, IonIcon],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
export class AppComponent {
  constructor(private router: Router) {
    addIcons({
      addOutline,
      arrowBackOutline,
      calendarOutline,
      homeOutline,
      personCircleOutline,
    });
  }

  get isProfileSection() {
    return ['/profile', '/goals', '/ai-chat'].includes(this.router.url);
  }

  back() {
    this.router.navigate([
      ['/goals', '/ai-chat'].includes(this.router.url) ? '/profile' : '/',
    ]);
  }
}
