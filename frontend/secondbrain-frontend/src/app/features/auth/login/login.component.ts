import { Component } from '@angular/core';
import { AuthService } from '../auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html'
})
export class LoginComponent {

   username = '';
   password = '';
   error = '';
   showPassword = false;

  constructor(private authService: AuthService,
    private router: Router
  ) {}
  ngOnInit() {
    if (this.authService.getToken()) {
      this.router.navigate(['/tasks']);
    }
    this.refreshIcons();
  }

  private refreshIcons() {
    setTimeout(() => {
      if ((window as any).lucide) {
        (window as any).lucide.createIcons();
      }
    }, 100);
  }


  onLogin() {
  this.authService.login(this.username, this.password)
  .subscribe({
    next: (res) => {
      if (!res?.token) {
        console.error('Login failed or malformed response', res);
        return;
      }

      this.authService.saveToken(res.token);
      this.authService.saveUser(this.username);
      this.router.navigate(['/tasks']);
    },
    error: (err) => {
      this.error = err.error?.message || 'Login failed. Please check your credentials.';
      console.error('Login error', err);
    }
  });

}

}
