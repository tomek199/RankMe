import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddPlayerComponent } from './add-player.component';
import { MatIconModule } from '@angular/material/icon';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { PlayerService } from '../shared/player.service';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { of } from 'rxjs';
import { SUBMITTED } from '../../../testing/data';
import { By } from '@angular/platform-browser';
import { CreatePlayerCommand } from '../shared/player.model';

describe('AddPlayerComponent', () => {
  let component: AddPlayerComponent;
  let fixture: ComponentFixture<AddPlayerComponent>;
  let playerServiceSpy = jasmine.createSpyObj('PlayerService', ['addPlayer']);
  let dialogRefSpy = jasmine.createSpyObj('MatDialogRef', ['close']);

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AddPlayerComponent ],
      imports: [
        FormsModule, ReactiveFormsModule, BrowserAnimationsModule,
        MatDialogModule, MatFormFieldModule, MatInputModule, MatIconModule
      ],
      providers: [
        { provide: MAT_DIALOG_DATA, useValue: 'league-1' },
        { provide: MatDialogRef, useValue: dialogRefSpy },
        { provide: PlayerService, useValue: playerServiceSpy }
      ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AddPlayerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it ('should mark form invalid when user name is empty', () => {
    expect(component.addPlayerForm.invalid).toBeTrue();
  });

  it('should add player', () => {
    const playerName = 'Test Player';
    playerServiceSpy.addPlayer.and.returnValue(of({data: {createPlayer: SUBMITTED}}));
    component.addPlayerForm.controls.name.setValue(playerName);
    expect(component.addPlayerForm.invalid).toBeFalse();
    const submitButton = fixture.debugElement.query(By.css('form'));
    submitButton.triggerEventHandler('submit', null);
    expect(playerServiceSpy.addPlayer).toHaveBeenCalledOnceWith(new CreatePlayerCommand('league-1', playerName));
    expect(dialogRefSpy.close).toHaveBeenCalledTimes(1);
    // todo expect subscription return
  });
});
