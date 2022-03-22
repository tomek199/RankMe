import { ComponentFixture, inject, TestBed } from '@angular/core/testing';

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
import { LeagueService } from '../../leagues/shared/league.service';
import { of } from 'rxjs';
import { COMPLETED_GAMES_PAGE, LEAGUE_WITH_PLAYERS } from '../../../testing/data';
import { By } from '@angular/platform-browser';
import { GameService } from '../shared/game.service';
import { PlayGameCommand } from '../shared/game.model';

describe('PlayGameComponent', () => {
  let component: PlayGameComponent;
  let fixture: ComponentFixture<PlayGameComponent>;
  let leagueServiceSpy = jasmine.createSpyObj('LeagueService', ['leagueWithPlayers']);
  let gameServiceSpy = jasmine.createSpyObj('GameService', ['playGame']);
  let snacbarServiceSpy = jasmine.createSpyObj('SnackbarService', ['showMessage']);

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
        { provide: SnackbarService, useValue: snacbarServiceSpy },
        { provide: LeagueService, useValue: leagueServiceSpy },
        { provide: GameService, useValue: gameServiceSpy }
      ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PlayGameComponent);
    component = fixture.componentInstance;
    leagueServiceSpy.leagueWithPlayers.and.returnValue(of({data: {league: LEAGUE_WITH_PLAYERS}}));
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should mark form invalid when players are incorrect', () => {
    component.playGameForm.controls.playerOneScore.setValue(3);
    component.playGameForm.controls.playerTwoScore.setValue(2);
    // both players incorrect
    component.playGameForm.controls.playerOne.setValue('Player One');
    component.playGameForm.controls.playerTwo.setValue('Player Two');
    fixture.detectChanges();
    expect(component.playGameForm.invalid).toBeTrue();
    expect(component.playGameForm.errors).toBeNull();
    expect(component.playGameForm.controls.playerOne.invalid).toBeTrue();
    expect(component.playGameForm.controls.playerOne.errors).toEqual({playerInvalid: true});
    expect(component.playGameForm.controls.playerTwo.invalid).toBeTrue();
    expect(component.playGameForm.controls.playerTwo.errors).toEqual({playerInvalid: true});
    // first player incorrect
    component.playGameForm.controls.playerTwo.setValue(LEAGUE_WITH_PLAYERS.players[0]);
    fixture.detectChanges();
    expect(component.playGameForm.invalid).toBeTrue();
    // second player incorrect
    component.playGameForm.controls.playerOne.setValue(LEAGUE_WITH_PLAYERS.players[0]);
    component.playGameForm.controls.playerTwo.setValue('Player Two');
    fixture.detectChanges();
    expect(component.playGameForm.invalid).toBeTrue();
  });

  it('should mark form invalid when selected players are the same', () => {
    component.playGameForm.controls.playerOne.setValue(LEAGUE_WITH_PLAYERS.players[1]);
    component.playGameForm.controls.playerTwo.setValue(LEAGUE_WITH_PLAYERS.players[1]);
    component.playGameForm.controls.playerOneScore.setValue(1);
    component.playGameForm.controls.playerTwoScore.setValue(2);
    expect(component.playGameForm.invalid).toBeTrue();
    expect(component.playGameForm.errors).toEqual({samePlayers: true});
  });

  it('should mark form invalid when selected draw is not allowed', () => {
    component.playGameForm.controls.playerOne.setValue(LEAGUE_WITH_PLAYERS.players[0]);
    component.playGameForm.controls.playerTwo.setValue(LEAGUE_WITH_PLAYERS.players[1]);
    component.playGameForm.controls.playerOneScore.setValue(1);
    component.playGameForm.controls.playerTwoScore.setValue(1);
    expect(component.playGameForm.invalid).toBeTrue();
    expect(component.playGameForm.errors).toEqual({drawNotAllowed: true});
  });

  it('should play game', () => {
    gameServiceSpy.playGame.and.returnValue(of({data: { playGame: 'SUBMITTED' }}));
    component.playGameForm.controls.playerOne.setValue(LEAGUE_WITH_PLAYERS.players[2]);
    component.playGameForm.controls.playerTwo.setValue(LEAGUE_WITH_PLAYERS.players[1]);
    component.playGameForm.controls.playerOneScore.setValue(3);
    component.playGameForm.controls.playerTwoScore.setValue(1);
    expect(component.playGameForm.invalid).toBeFalse();
    const submitButton = fixture.debugElement.query(By.css('form'));
    submitButton.triggerEventHandler('submit', null);
    expect(gameServiceSpy.playGame).toHaveBeenCalledWith(
      new PlayGameCommand(LEAGUE_WITH_PLAYERS.players[2].id, LEAGUE_WITH_PLAYERS.players[1].id, 3, 1)
    );
  });
});
