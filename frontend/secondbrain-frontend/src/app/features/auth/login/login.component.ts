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
  ngOnInit() {
  if (this.authService.getToken()) {
    this.router.navigate(['/tasks']);
  }
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
      this.router.navigate(['/tasks']);
    },
    error: (err) => {
      console.error('Login error', err);
    }
  });

}

}
