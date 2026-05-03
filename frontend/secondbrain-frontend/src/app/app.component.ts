import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from './features/auth/auth.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html'
})
export class AppComponent {
  isDarkMode = true;

  constructor(
    private authService: AuthService,
    public router: Router
  ) {
    const savedTheme = localStorage.getItem('theme');
    if (savedTheme) {
      this.isDarkMode = savedTheme !== 'light';
    } else {
      this.isDarkMode = window.matchMedia('(prefers-color-scheme: dark)').matches;
    }
    this.applyTheme();
  }

  get username(): string {
    return this.authService.getUser() || 'User';
  }

  toggleDarkMode() {
    this.isDarkMode = !this.isDarkMode;
    localStorage.setItem('theme', this.isDarkMode ? 'dark' : 'light');
    this.applyTheme();
    
    // Refresh icons
    setTimeout(() => {
      if ((window as any).lucide) {
        (window as any).lucide.createIcons();
      }
    }, 10);
  }

  private applyTheme() {
    if (this.isDarkMode) {
      document.body.classList.remove('light-theme');
      document.body.classList.add('dark-theme');
    } else {
      document.body.classList.remove('dark-theme');
      document.body.classList.add('light-theme');
    }
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  isLoginPage(): boolean {
    const url = this.router.url;
    return url === '/auth/login' || url === '/auth/register' || url.startsWith('/auth/');
  }

  isAiPage(): boolean {
    return this.router.url.includes('/ai');
  }
}
