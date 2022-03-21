import { Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';

@Injectable({
  providedIn: 'root'
})
export class SnackbarService {

  constructor(private snackbar: MatSnackBar) { }

  handleError = (error: any) => {
    let snackbarMessage: string;
    if (error.graphQLErrors) snackbarMessage = 'API error occurred';
    else if (error.networkError) snackbarMessage =`Network error occurred`;
    else snackbarMessage = 'Internal error occurred';
    this.snackbar.open(snackbarMessage, undefined, {
      verticalPosition: 'top', duration: 5000
    });
  }

  showMessage(message: string): void {
    this.snackbar.open(message, undefined, {
      verticalPosition: 'bottom', duration: 3000
    });
  }
}
