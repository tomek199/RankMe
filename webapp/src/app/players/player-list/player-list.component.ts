import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { PlayerService } from '../shared/player.service';
import { SnackbarService } from '../../shared/snackbar/snackbar.service';
import { Player } from '../../shared/model/player';
import { MatDialog } from '@angular/material/dialog';
import { AddPlayerComponent } from '../add-player/add-player.component';
import { Subscription } from 'rxjs';
import { MatTableDataSource } from '@angular/material/table';

@Component({
  selector: 'app-player-list',
  templateUrl: './player-list.component.html',
  styleUrls: ['./player-list.component.scss']
})
export class PlayerListComponent implements OnInit, OnDestroy {
  isLoading: boolean = false;
  leagueId: string;
  displayedColumns = ['username', 'deviation', 'rating'];
  dataSource: MatTableDataSource<Player> = new MatTableDataSource<Player>();
  playerCreatedSubscription: Subscription;

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
      this.subscribePlayerCreated();
    });
  }

  ngOnDestroy(): void {
    this.playerCreatedSubscription?.unsubscribe();
  }

  getPlayers() {
    this.playerService.players(this.leagueId)
      .subscribe({
        next: ({data}) => {
          this.dataSource.data = data.players;
        },
        error: this.snackbarService.handleError,
        complete: () => this.isLoading = false
      });
  }

  subscribePlayerCreated() {
    this.playerCreatedSubscription = this.playerService.playerCreated(this.leagueId)
      .subscribe({
        next: ({data}) => {
          this.snackbarService.showMessage(`Player "${data?.playerCreated.name}" created`);
          this.dataSource.data = [...this.dataSource.data, data?.playerCreated as Player];
        },
        error: this.snackbarService.handleError
      });
  }

  openAddPlayerDialog() {
    this.dialog.open(AddPlayerComponent, {data: this.leagueId});
  }
}
