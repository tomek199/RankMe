import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PlayGameComponent } from './play-game.component';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { SnackbarService } from '../../shared/snackbar/snackbar.service';
import { SnackbarServiceStub } from '../../../testing/stubs';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatFormFieldModule } from '@angular/material/form-field';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { MatSliderModule } from '@angular/material/slider';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

describe('PlayGameComponent', () => {
  let component: PlayGameComponent;
  let fixture: ComponentFixture<PlayGameComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PlayGameComponent ],
      imports: [
        FormsModule, ReactiveFormsModule, BrowserAnimationsModule,
        MatDialogModule, MatFormFieldModule, MatInputModule, MatAutocompleteModule, MatSliderModule, MatIconModule
      ],
      providers: [
        { provide: MAT_DIALOG_DATA, useValue: 'league-1' },
        { provide: MatDialogRef, useValue: {} },
        { provide: SnackbarService, useClass: SnackbarServiceStub },
      ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PlayGameComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
