import { Component, Input, OnInit } from '@angular/core';
import { Page, PageInfo } from '../../shared/model/page';
import { Game } from '../../shared/model/game';

@Component({
  selector: 'app-game-list-preview',
  templateUrl: './game-list-preview.component.html',
  styleUrls: ['./game-list-preview.component.scss']
})
export class GameListPreviewComponent implements OnInit {
  @Input() leagueId: string;
  @Input() set gamesPage(gamesPage: Page<Game>) {
    this.pageInfo = gamesPage.pageInfo;
    this.games = gamesPage.edges.map(edge => edge.node);
  }
  pageInfo: PageInfo;
  games: Game[];
  displayedColumns = ['datetime', 'player', 'score'];

  constructor() { }

  ngOnInit(): void { }
}
