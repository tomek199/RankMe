import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { LeagueService } from '../../leagues/shared/league.service';
import { ErrorHandlerService } from '../../shared/error-handler/error-handler.service';
import { League } from '../../shared/model/league';
import { AbstractControl, FormControl, FormGroup, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { Observable, of } from 'rxjs';
import { Player } from '../../shared/model/player';
import { GameService } from '../shared/game.service';
import { PlayGameCommand } from '../shared/game.model';

@Component({
  selector: 'app-play-game',
  templateUrl: './play-game.component.html',
  styleUrls: ['./play-game.component.scss']
})
export class PlayGameComponent implements OnInit {
  isLoading: boolean = false;
  league: League;
  filteredPlayers: Observable<Player[]>;
  playGameForm: FormGroup;

  constructor(
    @Inject(MAT_DIALOG_DATA) private leagueId: string,
    private dialogRef: MatDialogRef<PlayGameComponent>,
    private leagueService: LeagueService,
    private gameService: GameService,
    private errorHandler: ErrorHandlerService
  ) {
    this.playGameForm = new FormGroup({
      playerOne: new FormControl(null, [Validators.required, this.playerValidator()]),
      playerTwo: new FormControl(null, [Validators.required, this.playerValidator()]),
      playerOneScore: new FormControl(0),
      playerTwoScore: new FormControl(0)
    }, [this.drawValidator(), this.samePlayersValidator()]);
  }

  playerValidator(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (typeof control.value === 'string') {
        return { playerInvalid: true };
      }
      return null;
    }
  }

  drawValidator(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const form = control as FormGroup;
      if (this.league && !this.league.allowDraws) {
        if (form.value.playerOneScore == form.value.playerTwoScore) {
          return { drawNotAllowed: true };
        }
      }
      return null;
    }
  }

  samePlayersValidator(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const form = control as FormGroup;
      if (form.value.playerOne == form.value.playerTwo) {
        return { samePlayers: true };
      }
      return null;
    }
  }

  ngOnInit(): void {
    this.getLeague();
    this.playGameForm.controls.playerOne.valueChanges.subscribe(value => {
      this.filteredPlayers = of(this.filter(value));
    });
    this.playGameForm.controls.playerTwo.valueChanges.subscribe(value => {
      this.filteredPlayers = of(this.filter(value));
    });
  }

  getLeague() {
    this.isLoading = true;
    this.leagueService.leagueWithPlayers(this.leagueId)
      .subscribe(({data}) => {
        this.league = data.league;
      }, this.errorHandler.handle).add(() => this.isLoading = false);
  }

  private filter(value: any): Player[] {
    return typeof value === 'string'
      ? this.league.players.filter(player => player.name.toLowerCase().includes(value.toLowerCase()))
      : [];
  }

  displayPlayerName(player: any) {
    if (player) return player.name
  }

  showGeneralValidationError() {
    return this.playGameForm.controls.playerOne.valid && this.playGameForm.controls.playerTwo.valid;
  }

  playGame() {
    const command = new PlayGameCommand(
      this.playGameForm.value.playerOne.id, this.playGameForm.value.playerTwo.id,
      this.playGameForm.value.playerOneScore, this.playGameForm.value.playerTwoScore
    );
    this.gameService.playGame(command).subscribe(() => {
      this.isLoading = true;
      window.setTimeout(() => this.dialogRef.close(true), 1000);
    })
  }
}
