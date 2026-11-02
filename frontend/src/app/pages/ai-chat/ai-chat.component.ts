import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { IonIcon } from '@ionic/angular';
import { addIcons } from 'ionicons';
import { sendOutline, sparklesOutline } from 'ionicons/icons';
import { SelectedDateService } from '../../shared/services/selected-date.service';

type ChatMessage = {
  role: 'assistant' | 'user';
  text: string;
};

@Component({
  standalone: true,
  imports: [CommonModule, FormsModule, IonIcon],
  templateUrl: './ai-chat.component.html',
  styleUrl: './ai-chat.component.scss',
})
export class AiChatComponent {
  message = '';
  isLoading = false;
  error = '';
  messages: ChatMessage[] = [
    {
      role: 'assistant',
      text: 'Hi! Ask me for a meal idea, macro advice, or help reaching your daily goal.',
    },
  ];

  constructor(
    private http: HttpClient,
    private selectedDate: SelectedDateService,
  ) {
    addIcons({ sendOutline, sparklesOutline });
  }

  get dateLabel() {
    return this.selectedDate.format(this.selectedDate.value);
  }

  askSuggestion(question: string) {
    this.message = question;
    this.send();
  }

  send() {
    const text = this.message.trim();
    if (!text || this.isLoading) return;

    const history = this.messages.slice(-12);
    this.messages.push({ role: 'user', text });
    this.message = '';
    this.error = '';
    this.isLoading = true;

    this.http
      .post<{ answer: string }>('/api/ai/chat', {
        message: text,
        date: this.selectedDate.value,
        history,
      })
      .subscribe({
        next: (response) => {
          this.messages.push({
            role: 'assistant',
            text: response.answer.trim(),
          });
          this.isLoading = false;
        },
        error: () => {
          this.error = 'I could not reach the AI assistant. Please try again.';
          this.isLoading = false;
        },
      });
  }
}
