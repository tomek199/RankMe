import { Component } from '@angular/core';
import { SnackbarService } from '../../shared/snackbar/snackbar.service';
import { MatDialogRef } from '@angular/material/dialog';
import { LeagueService } from '../shared/league.service';
import { PlayGameComponent } from '../../games/play-game/play-game.component';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { CreateLeagueCommand } from '../shared/league.model';

@Component({
  selector: 'app-create-league',
  templateUrl: './create-league.component.html',
  styleUrls: ['./create-league.component.scss']
})
export class CreateLeagueComponent {
  createLeagueForm: FormGroup;

  constructor(
    private dialogRef: MatDialogRef<PlayGameComponent>,
    private leagueService: LeagueService,
    private snackbarService: SnackbarService
  ) {
    this.createLeagueForm = new FormGroup({
      name: new FormControl(null, Validators.required)
    });
  }

  createLeague() {
    const command = new CreateLeagueCommand(this.createLeagueForm.value.name);
    this.leagueService.createLeague(command).subscribe(() => {
      this.dialogRef.close(this.snackbarService.showMessage('League created!'));
    });
  }
}
