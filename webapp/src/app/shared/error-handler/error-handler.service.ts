import { Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';

@Injectable({
  providedIn: 'root'
})
export class ErrorHandlerService {

  constructor(private snackbar: MatSnackBar) { }

  handle = (error: any) => {
    let snackbarMessage: string;
    if (error.graphQLErrors) snackbarMessage = 'API error occurred';
    else if (error.networkError) snackbarMessage =`Network error occurred`;
    else snackbarMessage = 'Internal error occurred';
    this.snackbar.open(snackbarMessage, undefined, {
      verticalPosition: 'top', duration: 5000
    });
  }
}
