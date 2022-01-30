import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RecentlyPlayedGames } from './recently-played-games.component';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { GameService } from '../shared/game.service';
import { COMPLETED_GAMES_PAGE, LEAGUE_WITH_PLAYERS_AND_GAMES } from '../../../testing/data';
import { of } from 'rxjs';
import { By } from '@angular/platform-browser';

describe('RecentlyPlayedGamesComponent', () => {
  let component: RecentlyPlayedGames;
  let fixture: ComponentFixture<RecentlyPlayedGames>;
  let gameServiceSpy = jasmine.createSpyObj('GameService', ['completedGames']);

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RecentlyPlayedGames ],
      imports: [ MatCardModule, MatTableModule, MatIconModule, MatButtonModule, MatProgressBarModule ],
      providers: [
        { provide: GameService, useValue: gameServiceSpy }
      ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RecentlyPlayedGames);
    component = fixture.componentInstance;
    component.leagueId = 'league-1';
    component.gamesPage = LEAGUE_WITH_PLAYERS_AND_GAMES.completedGames;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show "No data" row when there is no games', () => {
    component.gamesPage = {
      pageInfo: {hasNextPage: false, endCursor: null},
      edges: []
    }
    fixture.detectChanges();
    const noDataRow = fixture.nativeElement.querySelector('table tbody tr td');
    expect(noDataRow.textContent).toEqual("No data");
  });

  it('should show games in table', () => {
    const rows = fixture.nativeElement.querySelectorAll('table tbody tr');
    rows.forEach((row: Element, index: number) => {
      const columns = row.querySelectorAll('td');
      const datetime = columns[0].querySelectorAll('p');
      expect(datetime[0].textContent).toBeDefined();
      expect(datetime[1].textContent).toEqual(LEAGUE_WITH_PLAYERS_AND_GAMES.completedGames.edges[index].node.dateTime.substring(11, 16))
      const players = columns[1].querySelectorAll('p');
      expect(players[0].textContent).toEqual(LEAGUE_WITH_PLAYERS_AND_GAMES.completedGames.edges[index].node.playerOneName);
      expect(players[1].textContent).toEqual(LEAGUE_WITH_PLAYERS_AND_GAMES.completedGames.edges[index].node.playerTwoName);
      const result = columns[2].querySelectorAll('p');
      expect(result[0].textContent).toEqual(LEAGUE_WITH_PLAYERS_AND_GAMES.completedGames.edges[index].node.result.playerOneScore.toString())
      expect(result[1].textContent).toEqual(LEAGUE_WITH_PLAYERS_AND_GAMES.completedGames.edges[index].node.result.playerTwoScore.toString());
    })
  });

  it('should load more games after button click', () => {
    gameServiceSpy.completedGames.and.returnValue(of({data: { completedGames: COMPLETED_GAMES_PAGE }}));
    const loadMoreButton = fixture.debugElement.query(By.css('button.mat-raised-button'));
    loadMoreButton.triggerEventHandler('click', null)
    fixture.detectChanges()
    const rows = fixture.nativeElement.querySelectorAll('table tbody tr');
    expect(rows.length).toEqual(6);
  });
});