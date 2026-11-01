import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { IonSelect, IonSelectOption } from '@ionic/angular';

@Component({
  standalone: true,
  imports: [CommonModule, FormsModule, IonSelect, IonSelectOption],
  templateUrl: './goals.component.html',
  styleUrl: './goals.component.scss',
})
export class GoalsComponent {
  profile = {
    age: null as number | null,
    weightKg: null as number | null,
    heightCm: null as number | null,
    sex: 'MALE',
    activityLevel: 'SEDENTARY',
    goal: 'MAINTAIN',
  };
  goals = { calories: 2000, protein: 120, carbs: 250, fat: 65 };
  saved = false;

  constructor(private http: HttpClient) {
    this.http.get<any>('/api/profile').subscribe((data) => {
      this.profile = {
        age: data.age,
        weightKg: data.weightKg,
        heightCm: data.heightCm,
        sex: data.sex || 'MALE',
        activityLevel: data.activityLevel || 'SEDENTARY',
        goal:
          data.goal === 'GAIN_075'
            ? 'GAIN_050'
            : data.goal === 'LOSE'
              ? 'LOSE_050'
              : data.goal === 'GAIN'
                ? 'GAIN_025'
                : data.goal || 'MAINTAIN',
      };
      this.goals = data.goals;
      this.updatePreview();
    });
  }

  save() {
    this.http.put<any>('/api/profile', this.profile).subscribe((data) => {
      this.goals = data.goals;
      this.saved = true;
      setTimeout(() => (this.saved = false), 2500);
    });
  }

  updatePreview() {
    const { age, weightKg, heightCm, sex, activityLevel, goal } = this.profile;
    if (!age || !weightKg || !heightCm) return;

    const multipliers: Record<string, number> = {
      SEDENTARY: 1.2,
      LIGHT: 1.375,
      MODERATE: 1.55,
      ACTIVE: 1.725,
    };
    const adjustments: Record<string, number> = {
      LOSE_025: -275,
      LOSE_050: -550,
      MAINTAIN: 0,
      GAIN_025: 275,
      GAIN_050: 550,
    };
    const bmr =
      10 * weightKg + 6.25 * heightCm - 5 * age + (sex === 'FEMALE' ? -161 : 5);
    const calories = Math.max(
      sex === 'FEMALE' ? 1200 : 1500,
      bmr * multipliers[activityLevel] + adjustments[goal],
    );
    const protein = weightKg * 1.8;
    const fat = (calories * 0.25) / 9;
    const carbs = Math.max(0, (calories - protein * 4 - fat * 9) / 4);
    this.goals = {
      calories: Math.round(calories),
      protein: Math.round(protein),
      carbs: Math.round(carbs),
      fat: Math.round(fat),
    };
  }
}
