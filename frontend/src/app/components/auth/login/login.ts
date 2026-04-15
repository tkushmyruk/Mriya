import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login.html',
  styleUrls: ['./login.css']
})
export class LoginComponent {
  loginForm: FormGroup;
  verifyForm: FormGroup;

  errorMessage: string = '';
  isLoginMode = true;
  isVerifyMode = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      phone: ['', Validators.pattern(/^\+?\d{10,15}$/)],
      password: ['', [Validators.required, Validators.minLength(3)]],
      firstName: [''],
      lastName: ['']
    });

    this.verifyForm = this.fb.group({
      code: ['', [Validators.required, Validators.minLength(6), Validators.maxLength(6)]]
    });
  }

  toggleMode() {
    this.isLoginMode = !this.isLoginMode;
    this.isVerifyMode = false;
    this.errorMessage = '';

    const firstNameControl = this.loginForm.get('firstName');
    const lastNameControl = this.loginForm.get('lastName');

    if (!this.isLoginMode) {
      firstNameControl?.setValidators([Validators.required]);
      lastNameControl?.setValidators([Validators.required]);
    } else {
      firstNameControl?.clearValidators();
      lastNameControl?.clearValidators();
    }

    firstNameControl?.updateValueAndValidity();
    lastNameControl?.updateValueAndValidity();
  }

  onSubmit() {
    if (this.loginForm.invalid) return;
    this.errorMessage = '';

    if (this.isLoginMode) {
      this.handleLogin();
    } else {
      this.handleRegister();
    }
  }

  private handleLogin() {
    this.authService.login(this.loginForm.value).subscribe({
      next: () => {
        this.router.navigate(['/profile/me']);
      },
      error: (err) => {
        if (err.error === 'USER_DISABLED' || err.status === 403) {
          this.isVerifyMode = true;
        } else {
          this.errorMessage = 'Невірний Email або пароль';
        }
      }
    });
  }

  private handleRegister() {
    this.authService.register(this.loginForm.value).subscribe({
      next: () => {
        this.isVerifyMode = true;
      },
      error: (err) => {
        this.errorMessage = err.error || 'Помилка реєстрації. Можливо, Email вже зайнятий.';
      }
    });
  }

  onVerifyCode() {
    if (this.verifyForm.invalid) return;

    const codeValue = this.verifyForm.get('code')?.value;

    this.authService.verifyCode(codeValue).subscribe({
      next: (res) => {
        this.router.navigate(['/profile/me']);
      },
      error: (err) => {
        this.errorMessage = err.error || 'Невірний або прострочений код';
      }
    });
  }

  onCodeInput() {
    const code = this.verifyForm.get('code')?.value;
    if (code && code.length === 6) {
      this.onVerifyCode();
    }
  }

  backToLogin() {
    this.isVerifyMode = false;
    this.errorMessage = '';
  }
}
