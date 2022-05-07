import { inject, TestBed } from '@angular/core/testing';

import { SnackbarService } from './snackbar.service';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { of } from 'rxjs';
import { RouterTestingModule } from '@angular/router/testing';
import { Router } from '@angular/router';

describe('SnackbarService', () => {
  let service: SnackbarService;
  let snackbarSpy = jasmine.createSpyObj('MatSnackBar', ['open']);
  let snackbarRefSpy = jasmine.createSpyObj('MatSnackBarRef', ['afterDismissed', 'onAction']);

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ MatSnackBarModule, RouterTestingModule ],
      providers: [
        { provide: MatSnackBar, useValue: snackbarSpy }
      ]
    });
    service = TestBed.inject(SnackbarService);
    snackbarSpy.open.and.returnValue(snackbarRefSpy);
    snackbarRefSpy.afterDismissed.and.returnValue(of(true));
    snackbarRefSpy.onAction.and.returnValue(of(true));
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
    const matSnackBar = service.showMessage(message);
    expect(matSnackBar).toBeTruthy();
    expect(snackbarSpy.open).toHaveBeenCalledWith(
      message,
      undefined,
      {verticalPosition: 'bottom', duration: 3000}
    );
  });

  it('should show snackbar with given message and action', inject([Router], (router: Router) => {
    spyOn(router, 'navigateByUrl').and.stub();
    const message = 'Command submitted!';
    const action = 'Open link';
    const url = '/goto/link';
    const matSnackBar = service.showMessageWithAction(message, action, url);
    expect(matSnackBar).toBeTruthy();
    expect(snackbarSpy.open).toHaveBeenCalledWith(
      message,
      action,
      {verticalPosition: 'bottom', duration: 5000}
    );
    expect(router.navigateByUrl).toHaveBeenCalledWith(url);
  }));
});
