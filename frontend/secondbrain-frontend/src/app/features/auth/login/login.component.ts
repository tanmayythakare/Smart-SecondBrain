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

  constructor(private authService: AuthService,
    private router: Router
  ) {}

  onLogin() {
  this.authService.login(this.username, this.password)
    .subscribe({
      next: (res) => {
        this.authService.saveToken(res.body.token);
        this.router.navigate(['/tasks']); // ✅ FIX
      },
      error: () => {
        alert('Login failed');
      }
    });
}

}
