import { TestBed } from '@angular/core/testing';

import { ErrorHandlerService } from './error-handler.service';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';

describe('ErrorHandlerService', () => {
  let service: ErrorHandlerService;
  let snackbarSpy = jasmine.createSpyObj('MatSnackBar', ['open']);

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ MatSnackBarModule ],
      providers: [
        { provide: MatSnackBar, useValue: snackbarSpy }
      ]
    });
    service = TestBed.inject(ErrorHandlerService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should show snackbar with GraphQL error message', () => {
    const error = {graphQLErrors: 'GraphQL error'};
    service.handle(error);
    expect(snackbarSpy.open).toHaveBeenCalledWith(
      'API error occurred',
      undefined,
      {verticalPosition: 'top', duration: 5000}
    );
  });

  it('should show snackbar with network error message', () => {
    const error = {networkError: 'Network error'};
    service.handle(error);
    expect(snackbarSpy.open).toHaveBeenCalledWith(
      'Network error occurred',
      undefined,
      {verticalPosition: 'top', duration: 5000}
    );
  });

  it('should show snackbar with default error message', () => {
    const error = {internalError: 'Internal error'};
    service.handle(error);
    expect(snackbarSpy.open).toHaveBeenCalledWith(
      'Internal error occurred',
      undefined,
      {verticalPosition: 'top', duration: 5000}
    );
  });
});
