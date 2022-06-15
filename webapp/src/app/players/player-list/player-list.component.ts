import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { PlayerService } from '../shared/player.service';
import { SnackbarService } from '../../shared/snackbar/snackbar.service';
import { Player } from '../../shared/model/player';

@Component({
  selector: 'app-player-list',
  templateUrl: './player-list.component.html',
  styleUrls: ['./player-list.component.scss']
})
export class PlayerListComponent implements OnInit {
  isLoading: boolean = false;
  players: Player[] = [];
  displayedColumns = ['username', 'deviation', 'rating'];

  constructor(
    private route: ActivatedRoute,
    private playerService: PlayerService,
    private snackbarService: SnackbarService
  ) { }

  ngOnInit(): void {
    this.getPlayers();
  }

  getPlayers() {
    this.route.parent?.params.subscribe(params => {
      this.isLoading = true;
      const leagueId = params['league_id'];
      this.playerService.players(leagueId)
        .subscribe({
          next: ({data}) => {
            this.players = data.players
          },
          error: this.snackbarService.handleError,
          complete: () => this.isLoading = false
        });
    });
  }
}
