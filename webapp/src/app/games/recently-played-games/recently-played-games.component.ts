import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Page, PageInfo } from '../../shared/model/page';
import { CompletedGame } from '../../shared/model/game';
import { GameService } from '../shared/game.service';
import { MatTableDataSource } from '@angular/material/table';
import { SnackbarService } from '../../shared/snackbar/snackbar.service';
import { MatDialog } from '@angular/material/dialog';
import { PlayGameComponent } from '../play-game/play-game.component';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-recently-played-games',
  templateUrl: './recently-played-games.component.html',
  styleUrls: ['./recently-played-games.component.scss']
})
export class RecentlyPlayedGames {
  @Input() leagueId: string;
  @Input() set gamesPage(gamesPage: Page<CompletedGame>) {
    this.pageInfo = gamesPage.pageInfo;
    this.dataSource = new MatTableDataSource<CompletedGame>(gamesPage.edges.map(edge => edge.node))
  }
  @Output() gamePlayed = new EventEmitter<boolean>();
  private PAGE_SIZE = 5;
  isLoading: boolean = false;
  pageInfo: PageInfo;
  displayedColumns = ['datetime', 'players', 'score'];
  dataSource: MatTableDataSource<CompletedGame>

  constructor(
    private gameService: GameService,
    private snackbarService: SnackbarService,
    private dialog: MatDialog
  ) { }

  loadMore() {
    this.isLoading = true;
    this.gameService.completedGamesAfter(this.leagueId, this.PAGE_SIZE, this.pageInfo.endCursor!).subscribe(({data}) => {
      this.pageInfo = data.completedGames.pageInfo;
      this.dataSource.data = [...this.dataSource.data, ...data.completedGames.edges.map(edge => edge.node)]
    }, this.snackbarService.handleError).add(() => this.isLoading = false);
  }

  playGame() {
    const dialogRef = this.dialog.open(PlayGameComponent, {data: this.leagueId});
    dialogRef.afterClosed().subscribe((event: Observable<any>) => {
      if (event) event.subscribe(() => this.gamePlayed.emit(true));
    });
  }
}
