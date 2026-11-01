import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class SelectedDateService {
  private selectedDate = new Date().toISOString().slice(0, 10);

  get value() {
    return this.selectedDate;
  }

  set value(date: string) {
    this.selectedDate = date;
  }

  format(date: string) {
    if (date === new Date().toISOString().slice(0, 10)) {
      return 'Today';
    }

    return date.split('-').reverse().join('-');
  }
}
