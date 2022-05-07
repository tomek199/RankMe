import { Injectable } from '@angular/core';
import { MatSnackBar, MatSnackBarDismiss } from '@angular/material/snack-bar';
import { Observable } from 'rxjs';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class SnackbarService {

  constructor(
    private snackbar: MatSnackBar,
    private router: Router
  ) { }

  handleError = (error: any) => {
    let snackbarMessage: string;
    if (error.graphQLErrors) snackbarMessage = 'API error occurred';
    else if (error.networkError) snackbarMessage =`Network error occurred`;
    else snackbarMessage = 'Internal error occurred';
    this.snackbar.open(snackbarMessage, undefined, {
      verticalPosition: 'top', duration: 5000
    });
  }

  showMessage(message: string): Observable<MatSnackBarDismiss> {
    return this.snackbar.open(message, undefined, {
      verticalPosition: 'bottom', duration: 3000
    }).afterDismissed();
  }

  showMessageWithAction(message: string, action: string, url: string) {
    return this.snackbar.open(message, action, {
      verticalPosition: 'bottom', duration: 5000
    }).onAction().subscribe(() => this.router.navigateByUrl(url));
  }
}
