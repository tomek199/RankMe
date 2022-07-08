import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { PlayerService } from '../shared/player.service';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { CreatePlayerCommand } from '../shared/player.model';

@Component({
  selector: 'app-add-player',
  templateUrl: './add-player.component.html',
  styleUrls: ['./add-player.component.scss']
})
export class AddPlayerComponent {
  addPlayerForm: FormGroup;

  constructor(
    @Inject(MAT_DIALOG_DATA) private leagueId: string,
    private dialogRef: MatDialogRef<AddPlayerComponent>,
    private playerService: PlayerService
  ) {
    this.addPlayerForm = new FormGroup({
      name: new FormControl(null, Validators.required)
    });
  }

  addPlayer() {
    const command = new CreatePlayerCommand(this.leagueId, this.addPlayerForm.value.name);
    this.playerService.addPlayer(command).subscribe(() => {
      this.dialogRef.close(true);
    });
  }
}
