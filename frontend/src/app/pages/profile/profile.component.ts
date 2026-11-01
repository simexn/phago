import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { IonIcon } from '@ionic/angular';
import { addIcons } from 'ionicons';
import { chevronForwardOutline, flagOutline } from 'ionicons/icons';

@Component({
  standalone: true,
  imports: [RouterLink, IonIcon],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.scss',
})
export class ProfileComponent {
  constructor() {
    addIcons({ chevronForwardOutline, flagOutline });
  }
}
