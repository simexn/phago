import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { IonIcon } from '@ionic/angular';
import { addIcons } from 'ionicons';
import {
  chevronBackOutline,
  chevronForwardOutline,
  searchOutline,
} from 'ionicons/icons';
import { SelectedDateService } from '../../shared/services/selected-date.service';

@Component({
  standalone: true,
  imports: [CommonModule, FormsModule, IonIcon],
  templateUrl: './add-food.component.html',
  styleUrl: './add-food.component.scss',
})
export class AddFoodComponent {
  query = '';
  products: any[] = [];
  date: string;

  constructor(
    private http: HttpClient,
    private router: Router,
    private selectedDate: SelectedDateService,
  ) {
    addIcons({ chevronBackOutline, chevronForwardOutline, searchOutline });
    this.date = selectedDate.value;
  }

  get dateLabel() {
    return this.selectedDate.format(this.date);
  }

  change(days: number) {
    const date = new Date(this.date + 'T12:00');
    date.setDate(date.getDate() + days);
    this.date = date.toISOString().slice(0, 10);
    this.selectedDate.value = this.date;
  }

  search() {
    const query = this.query.trim();
    if (query) {
      this.http
        .get<any[]>('/api/products/search', { params: { q: query } })
        .subscribe((products) => (this.products = products));
    }
  }

  add(product: any) {
    this.router.navigate(['/food'], { state: { product, date: this.date } });
  }
}
