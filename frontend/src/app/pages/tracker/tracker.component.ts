import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { IonIcon } from '@ionic/angular';
import { addIcons } from 'ionicons';
import {
  addOutline,
  arrowForwardOutline,
  chevronBackOutline,
  chevronDownOutline,
  chevronForwardOutline,
  removeOutline,
  waterOutline,
} from 'ionicons/icons';
import { SelectedDateService } from '../../shared/services/selected-date.service';

@Component({
  standalone: true,
  imports: [CommonModule, FormsModule, IonIcon],
  styleUrl: './tracker.component.scss',
  templateUrl: './tracker.component.html',
})
export class TrackerComponent {
  meals: any[] = [];
  calories = 0;
  protein = 0;
  carbs = 0;
  fat = 0;
  target = 2000;
  proteinTarget = 120;
  carbsTarget = 250;
  fatTarget = 65;
  sugar = 0;
  sugarTarget = 50;
  fiber = 0;
  fiberTarget = 30;
  water = 0;
  waterTarget = 2000;
  customWater = 300;
  waterStep = 250;
  showWaterOptions = false;
  date: string;

  constructor(
    private http: HttpClient,
    private router: Router,
    private selectedDate: SelectedDateService,
  ) {
    addIcons({
      addOutline,
      arrowForwardOutline,
      chevronBackOutline,
      chevronDownOutline,
      chevronForwardOutline,
      removeOutline,
      waterOutline,
    });
    this.date = selectedDate.value;
    this.load();
  }

  get progress() {
    return Math.min(100, Math.round((this.calories / this.target) * 100));
  }

  get caloriesRemaining() {
    return Math.round(this.target - this.calories);
  }

  get label() {
    return this.selectedDate.format(this.date);
  }

  get waterProgress() {
    return Math.min(100, Math.round((this.water / this.waterTarget) * 100));
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
      .get<any>('/api/dashboard', { params: { date: this.date } })
      .subscribe((data) => Object.assign(this, data));
    this.http
      .get<any[]>('/api/meals', { params: { date: this.date } })
      .subscribe((meals) => (this.meals = meals));
    this.http
      .get<any>('/api/water', { params: { date: this.date } })
      .subscribe((data) => {
        this.water = data.milliliters;
        this.waterTarget = data.target;
      });
  }

  showMeals() {
    this.router.navigate(['/meals']);
  }

  addWater(milliliters: number) {
    if (!Number.isFinite(milliliters) || milliliters === 0) return;
    this.http
      .post<any>('/api/water', { date: this.date, milliliters })
      .subscribe((data) => {
        this.water = data.milliliters;
        this.waterTarget = data.target;
      });
  }

  setWaterStep(amount: number) {
    this.waterStep = amount;
    this.showWaterOptions = false;
  }
}
