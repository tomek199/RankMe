import { Component, Input } from '@angular/core';
import { Page, PageInfo } from '../../shared/model/page';
import { ScheduledGame } from '../../shared/model/game';
import { MatTableDataSource } from '@angular/material/table';
import { GameService } from '../shared/game.service';
import { ErrorHandlerService } from '../../shared/error-handler/error-handler.service';

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
    private errorHandler: ErrorHandlerService
  ) { }

  loadMore() {
    this.isLoading = true;
    this.gameService.scheduledGames(this.leagueId, this.PAGE_SIZE, this.pageInfo.endCursor!).subscribe(({data}) => {
      this.pageInfo = data.scheduledGames.pageInfo;
      this.dataSource.data = [...this.dataSource.data, ...data.scheduledGames.edges.map(edge => edge.node)]
      this.isLoading = false;
    }, this.errorHandler.handle).add(() => this.isLoading = false);
  }
}
