import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';

@Component({
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './food-detail.component.html',
  styleUrl: './food-detail.component.scss',
})
export class FoodDetailComponent {
  product: any = history.state.product || {};
  date = history.state.date;
  grams = 100;
  meal = 'Breakfast';

  constructor(
    private http: HttpClient,
    public router: Router,
  ) {}

  add() {
    this.http
      .post('/api/meals', {
        date: this.date,
        mealType: this.meal,
        productName: this.product.name,
        grams: this.grams,
        caloriesPer100: this.product.calories,
        proteinPer100: this.product.protein,
        carbsPer100: this.product.carbs,
        fatPer100: this.product.fat,
        sugarPer100: this.product.sugar,
        fiberPer100: this.product.fiber,
      })
      .subscribe(() => this.router.navigate(['/']));
  }
}
