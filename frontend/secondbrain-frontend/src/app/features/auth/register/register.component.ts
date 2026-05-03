import { Component } from '@angular/core';
import { AuthService } from '../auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {
  email = '';
  username = '';
  password = '';
  confirmPassword = '';
  showPassword = false;
  error = '';

  constructor(private authService: AuthService, private router: Router) {}

  ngOnInit() {
    this.refreshIcons();
  }

  private refreshIcons() {
    setTimeout(() => {
      if ((window as any).lucide) {
        (window as any).lucide.createIcons();
      }
    }, 100);
  }

  onRegister() {
    if (this.password !== this.confirmPassword) {
      this.error = 'Passwords do not match';
      return;
    }

    this.authService.register(this.username, this.email, this.password)
      .subscribe({
        next: (res) => {
          if (res?.token) {
            this.authService.saveToken(res.token);
            this.authService.saveUser(this.username);
            this.router.navigate(['/tasks']);
          } else {
            this.router.navigate(['/auth/login']);
          }
        },
        error: (err: any) => {
          if (err.error?.errors) {
            // Extract the first validation error if present
            const firstError = Object.values(err.error.errors)[0];
            this.error = `Registration failed: ${firstError}`;
          } else {
            this.error = err.error?.message || 'Registration failed. User may already exist.';
          }
          console.error('Registration error', err);
        }
      });
  }
}
