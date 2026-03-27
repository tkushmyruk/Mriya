import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import {AuthService} from '../../services/auth.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login.html',
  styleUrls: ['./login.css']
})
export class LoginComponent {
  loginForm: FormGroup;
  errorMessage: string = '';
  isLoginMode = true;

  constructor(private fb: FormBuilder, private authService: AuthService, private router: Router) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      phone: ['', Validators.pattern(/^\+?\d{10,15}$/)],
      password: ['', [Validators.required, Validators.minLength(3)]],
      firstName: [''],
      lastName: ['']
    });
  }

  toggleMode() {
    this.isLoginMode = !this.isLoginMode;
    this.errorMessage = '';

    if (!this.isLoginMode) {
      this.loginForm.get('firstName')?.setValidators([Validators.required]);
      this.loginForm.get('lastName')?.setValidators([Validators.required]);
    } else {
      this.loginForm.get('firstName')?.clearValidators();
      this.loginForm.get('lastName')?.clearValidators();
    }
    this.loginForm.get('firstName')?.updateValueAndValidity();
    this.loginForm.get('lastName')?.updateValueAndValidity();
  }

  onSubmit() {
    if (this.loginForm.invalid) return;

    const request = this.isLoginMode
      ? this.authService.login(this.loginForm.value)
      : this.authService.register(this.loginForm.value);

    request.subscribe({
      next: (response) => {
        console.log(this.isLoginMode ? 'Вхід успішний' : 'Реєстрація успішна', response.token);
        this.router.navigate(['/profile/me']);
      },
      error: (err) => {
        this.errorMessage = this.isLoginMode ? 'Помилка входу' : 'Помилка реєстрації';
      }
    });
  }
}
