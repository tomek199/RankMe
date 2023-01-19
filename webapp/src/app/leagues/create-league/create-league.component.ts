import { Component, OnDestroy } from '@angular/core';
import { SnackbarService } from '../../shared/snackbar/snackbar.service';
import { MatDialogRef } from '@angular/material/dialog';
import { LeagueService } from '../shared/league.service';
import { PlayGameComponent } from '../../games/play-game/play-game.component';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { CreateLeagueCommand } from '../shared/league.model';
import { Subscription } from 'rxjs';
import { Router } from '@angular/router';

@Component({
  selector: 'app-create-league',
  templateUrl: './create-league.component.html',
  styleUrls: ['./create-league.component.scss']
})
export class CreateLeagueComponent implements OnDestroy {
  isLoading: boolean = false;
  createLeagueForm: FormGroup;
  leagueCreatedSubscription: Subscription;

  constructor(
    private dialogRef: MatDialogRef<PlayGameComponent>,
    private leagueService: LeagueService,
    private router: Router,
    private snackbarService: SnackbarService
  ) {
    this.createLeagueForm = new FormGroup({
      name: new FormControl(null, Validators.required)
    });
  }

  ngOnDestroy(): void {
    if (this.leagueCreatedSubscription) this.leagueCreatedSubscription.unsubscribe();
  }

  createLeague() {
    this.isLoading = true;
    const command = new CreateLeagueCommand(this.createLeagueForm.value.name);
    this.subscribeLeagueCreated(command.name);
    this.leagueService.createLeague(command).subscribe();
  }

  private subscribeLeagueCreated(name: string) {
    this.leagueCreatedSubscription = this.leagueService.leagueCreated(name)
      .subscribe({
        next: ({data}) => {
          const league = data!.leagueCreated;
          this.dialogRef.close(this.snackbarService.showMessage(`League ${league.name} created!`));
          this.router.navigate(['/leagues', league.id]);
        },
        error: this.snackbarService.handleError
      });
  }
}
