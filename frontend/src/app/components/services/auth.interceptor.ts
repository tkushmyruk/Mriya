import {catchError, throwError} from 'rxjs';
import {HttpErrorResponse, HttpInterceptorFn} from '@angular/common/http';
import {Router} from '@angular/router';
import {inject} from '@angular/core';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  const token = localStorage.getItem('access_token');

  let authReq = req;
  if (token) {
    authReq = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  return next(authReq).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401 || error.status === 403) {
        localStorage.removeItem('access_token');
        router.navigate(['/login']);
      }
      return throwError(() => error);
    })
  );
};
