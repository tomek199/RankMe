import { Component, Input, OnInit } from '@angular/core';
import { Page, PageInfo } from '../../shared/model/page';
import { Game } from '../../shared/model/game';
import { GameService } from '../shared/game.service';
import { MatTableDataSource } from '@angular/material/table';

@Component({
  selector: 'app-recently-played-games',
  templateUrl: './recently-played-games.component.html',
  styleUrls: ['./recently-played-games.component.scss']
})
export class RecentlyPlayedGames implements OnInit {
  @Input() leagueId: string;
  @Input() set gamesPage(gamesPage: Page<Game>) {
    this.pageInfo = gamesPage.pageInfo;
    this.dataSource = new MatTableDataSource<Game>(gamesPage.edges.map(edge => edge.node))
  }
  private PAGE_SIZE = 5;
  isLoading: boolean = false;
  pageInfo: PageInfo;
  displayedColumns = ['datetime', 'player', 'score'];
  dataSource: MatTableDataSource<Game>

  constructor(private gameService: GameService) { }

  ngOnInit(): void { }

  loadMore() {
    this.isLoading = true;
    this.gameService.games(this.leagueId, this.PAGE_SIZE, this.pageInfo.endCursor).subscribe(({data}) => {
      this.pageInfo = data.games.pageInfo;
      this.dataSource.data = [...this.dataSource.data, ...data.games.edges.map(edge => edge.node)]
      this.isLoading = false;
    });
  }
}
