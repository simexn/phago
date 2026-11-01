import { Routes } from '@angular/router';
import { TrackerComponent } from './pages/tracker/tracker.component';
import { FoodDetailComponent } from './pages/food-detail/food-detail.component';
import { MealsComponent } from './pages/meals/meals.component';
import { ProfileComponent } from './pages/profile/profile.component';
import { GoalsComponent } from './pages/goals/goals.component';
import { AddFoodComponent } from './pages/add-food/add-food.component';

export const routes: Routes = [
  { path: '', component: TrackerComponent },
  { path: 'add-food', component: AddFoodComponent },
  { path: 'food', component: FoodDetailComponent },
  { path: 'meals', component: MealsComponent },
  { path: 'profile', component: ProfileComponent },
  { path: 'goals', component: GoalsComponent },
];
