import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { IonIcon } from '@ionic/angular';
import { addIcons } from 'ionicons';
import { chevronBackOutline, chevronForwardOutline } from 'ionicons/icons';
import { SelectedDateService } from '../../shared/services/selected-date.service';

@Component({
  standalone: true,
  imports: [CommonModule, IonIcon],
  templateUrl: './meals.component.html',
  styleUrl: './meals.component.scss',
})
export class MealsComponent {
  date = '';
  meals: any[] = [];

  constructor(
    private http: HttpClient,
    private selectedDate: SelectedDateService,
  ) {
    addIcons({ chevronBackOutline, chevronForwardOutline });
    this.date = selectedDate.value;
    this.load();
  }

  get label() {
    return this.selectedDate.format(this.date);
  }

  change(days: number) {
    const date = new Date(this.date + 'T12:00');
    date.setDate(date.getDate() + days);
    this.date = date.toISOString().slice(0, 10);
    this.selectedDate.value = this.date;
    this.load();
  }

  load() {
    this.http
      .get<any[]>('/api/meals', { params: { date: this.date } })
      .subscribe((meals) => (this.meals = meals));
  }
}
