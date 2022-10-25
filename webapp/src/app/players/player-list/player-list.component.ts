import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { PlayerService } from '../shared/player.service';
import { SnackbarService } from '../../shared/snackbar/snackbar.service';
import { Player } from '../../shared/model/player';
import { MatDialog } from '@angular/material/dialog';
import { AddPlayerComponent } from '../add-player/add-player.component';

@Component({
  selector: 'app-player-list',
  templateUrl: './player-list.component.html',
  styleUrls: ['./player-list.component.scss']
})
export class PlayerListComponent implements OnInit {
  isLoading: boolean = false;
  leagueId: string;
  players: Player[] = [];
  displayedColumns = ['username', 'deviation', 'rating'];

  constructor(
    private route: ActivatedRoute,
    private playerService: PlayerService,
    private snackbarService: SnackbarService,
    private dialog: MatDialog
  ) { }

  ngOnInit(): void {
    this.route.parent?.params.subscribe(params => {
      this.isLoading = true;
      this.leagueId = params['league_id'];
      this.getPlayers();
    });
  }

  getPlayers() {
    this.playerService.players(this.leagueId)
      .subscribe({
        next: ({data}) => {
          this.players = data.players
        },
        error: this.snackbarService.handleError,
        complete: () => this.isLoading = false
      });
  }

  openAddPlayerDialog() {
    this.dialog.open(AddPlayerComponent, {data: this.leagueId});
  }
}
