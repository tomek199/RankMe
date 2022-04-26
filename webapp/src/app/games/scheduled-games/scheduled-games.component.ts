import { Component, Input } from '@angular/core';
import { Page, PageInfo } from '../../shared/model/page';
import { CompletedGame, ScheduledGame } from '../../shared/model/game';
import { MatTableDataSource } from '@angular/material/table';
import { GameService } from '../shared/game.service';
import { SnackbarService } from '../../shared/snackbar/snackbar.service';
import { ApolloQueryResult } from '@apollo/client';

@Component({
  selector: 'app-scheduled-games',
  templateUrl: './scheduled-games.component.html',
  styleUrls: ['./scheduled-games.component.scss']
})
export class ScheduledGamesComponent {
  @Input() leagueId: string;
  @Input() set gamesPage(gamesPage: Page<ScheduledGame>) {
    this.pageInfo = gamesPage.pageInfo;
    this.dataSource = new MatTableDataSource<ScheduledGame>(gamesPage.edges.map(edge => edge.node))
  }
  private PAGE_SIZE = 5;
  isLoading: boolean = false;
  pageInfo: PageInfo;
  displayedColumns = ['datetime', 'players'];
  dataSource: MatTableDataSource<ScheduledGame>

  constructor(
    private gameService: GameService,
    private snackbarService: SnackbarService
  ) { }

  loadPreviousPage() {
    this.isLoading = true;
    this.gameService.scheduledGamesBefore(this.leagueId, this.PAGE_SIZE, this.pageInfo.startCursor!).subscribe({
      next: this.updatePage,
      error: this.snackbarService.handleError,
      complete: () => this.isLoading = false
    });
  }

  loadNextPage() {
    this.isLoading = true;
    this.gameService.scheduledGamesAfter(this.leagueId, this.PAGE_SIZE, this.pageInfo.endCursor!).subscribe({
      next: this.updatePage,
      error: this.snackbarService.handleError,
      complete: () => this.isLoading = false
    });
  }

  private updatePage = (result: ApolloQueryResult<{ scheduledGames: Page<ScheduledGame> }>) => {
    this.pageInfo = result.data.scheduledGames.pageInfo;
    this.dataSource.data = result.data.scheduledGames.edges.map(edge => edge.node);
  }
}
