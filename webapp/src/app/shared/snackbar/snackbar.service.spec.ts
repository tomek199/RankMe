import { TestBed } from '@angular/core/testing';

import { SnackbarService } from './snackbar.service';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';

describe('SnackbarService', () => {
  let service: SnackbarService;
  let snackbarSpy = jasmine.createSpyObj('MatSnackBar', ['open']);

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ MatSnackBarModule ],
      providers: [
        { provide: MatSnackBar, useValue: snackbarSpy }
      ]
    });
    service = TestBed.inject(SnackbarService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should show snackbar with GraphQL error message', () => {
    const error = {graphQLErrors: 'GraphQL error'};
    service.handleError(error);
    expect(snackbarSpy.open).toHaveBeenCalledWith(
      'API error occurred',
      undefined,
      {verticalPosition: 'top', duration: 5000}
    );
  });

  it('should show snackbar with network error message', () => {
    const error = {networkError: 'Network error'};
    service.handleError(error);
    expect(snackbarSpy.open).toHaveBeenCalledWith(
      'Network error occurred',
      undefined,
      {verticalPosition: 'top', duration: 5000}
    );
  });

  it('should show snackbar with default error message', () => {
    const error = {internalError: 'Internal error'};
    service.handleError(error);
    expect(snackbarSpy.open).toHaveBeenCalledWith(
      'Internal error occurred',
      undefined,
      {verticalPosition: 'top', duration: 5000}
    );
  });

  it('should show snackbar with given message', () => {
    const message = 'Command submitted!';
    service.showMessage(message);
    expect(snackbarSpy.open).toHaveBeenCalledWith(
      message,
      undefined,
      {verticalPosition: 'bottom', duration: 3000}
    )
  });
});
