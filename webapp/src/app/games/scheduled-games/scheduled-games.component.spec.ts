import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ScheduledGamesComponent } from './scheduled-games.component';
import { LEAGUE_WITH_PLAYERS_AND_GAMES, SCHEDULED_GAMES_PAGE } from '../../../testing/data';
import { of } from 'rxjs';
import { By } from '@angular/platform-browser';
import { GameService } from '../shared/game.service';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { SnackbarService } from '../../shared/snackbar/snackbar.service';
import { SnackbarServiceStub } from '../../../testing/stubs';
import { Page } from '../../shared/model/page';
import { CompletedGame, ScheduledGame } from '../../shared/model/game';

describe('ScheduledGamesComponent', () => {
  let component: ScheduledGamesComponent;
  let fixture: ComponentFixture<ScheduledGamesComponent>;
  let gameServiceSpy = jasmine.createSpyObj('GameService', ['scheduledGamesBefore', 'scheduledGamesAfter']);

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ScheduledGamesComponent ],
      imports: [ MatCardModule, MatTableModule, MatIconModule, MatButtonModule, MatProgressBarModule ],
      providers: [
        { provide: GameService, useValue: gameServiceSpy },
        { provide: SnackbarService, useClass: SnackbarServiceStub }
      ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ScheduledGamesComponent);
    component = fixture.componentInstance;
    component.leagueId = 'league-1';
    component.gamesPage = LEAGUE_WITH_PLAYERS_AND_GAMES.scheduledGames;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show "No data" row when there is no scheduled games', () => {
    component.gamesPage = {
      pageInfo: {hasPreviousPage: false, hasNextPage: false, startCursor: null, endCursor: null},
      edges: []
    }
    fixture.detectChanges();
    const noDataRow = fixture.nativeElement.querySelector('table tbody tr td');
    expect(noDataRow.textContent).toEqual("No data");
  });

  it('should show scheduled games in table', () => {
    const rows = fixture.nativeElement.querySelectorAll('table tbody tr');
    rows.forEach((row: Element, index: number) => {
      const columns = row.querySelectorAll('td');
      const datetime = columns[0].querySelectorAll('span');
      expect(datetime[0].textContent).toBeDefined();
      expect(datetime[1].textContent).toEqual(LEAGUE_WITH_PLAYERS_AND_GAMES.scheduledGames.edges[index].node.dateTime.substring(11, 16))
      const players = columns[1].querySelectorAll('span');
      expect(players[0].textContent).toEqual(LEAGUE_WITH_PLAYERS_AND_GAMES.scheduledGames.edges[index].node.playerOneName);
      expect(players[1].textContent).toEqual(LEAGUE_WITH_PLAYERS_AND_GAMES.scheduledGames.edges[index].node.playerTwoName);
    })
  });

  it('should load next scheduled games page after button click', () => {
    const nextGamesPage = {
      pageInfo: {
        hasPreviousPage: true, hasNextPage: false, startCursor: 'game-1-cur', endCursor: 'game-1-cur'
      },
      edges: [
        {
          cursor: 'game-1-cur',
          node: {
            id: 'game-1', dateTime: '2021-12-30T15:38:05',
            playerOneId: 'player-1', playerOneName: 'Player-1', playerOneRating: 2300,
            playerTwoId: 'player-2', playerTwoName: 'Player-2', playerTwoRating: 1600
          }
        }
      ]
    } as Page<ScheduledGame>
    gameServiceSpy.scheduledGamesAfter.and.returnValue(of({data: { scheduledGames: nextGamesPage }}));
    const pageButtons = fixture.debugElement.queryAll(By.css('button.mat-raised-button'));
    pageButtons[1].triggerEventHandler('click', null)
    fixture.detectChanges()
    const rows = fixture.nativeElement.querySelectorAll('table tbody tr');
    expect(rows.length).toEqual(nextGamesPage.edges.length);
    expect(pageButtons[0].nativeElement.disabled).toBeFalse();
    expect(pageButtons[1].nativeElement.disabled).toBeTrue();
  });

  it('should load previous scheduled games page after button click', () => {
    const nextGamesPage = {
      pageInfo: {
        hasPreviousPage: false, hasNextPage: true, startCursor: 'game-1-cur', endCursor: 'game-1-cur'
      },
      edges: [
        {
          cursor: 'game-1-cur',
          node: {
            id: 'game-1', dateTime: '2021-12-30T15:38:05',
            playerOneId: 'player-1', playerOneName: 'Player-1', playerOneRating: 2300,
            playerTwoId: 'player-2', playerTwoName: 'Player-2', playerTwoRating: 1600
          }
        }
      ]
    } as Page<ScheduledGame>
    gameServiceSpy.scheduledGamesBefore.and.returnValue(of({data: { scheduledGames: nextGamesPage }}));
    const pageButtons = fixture.debugElement.queryAll(By.css('button.mat-raised-button'));
    pageButtons[0].triggerEventHandler('click', null)
    fixture.detectChanges()
    const rows = fixture.nativeElement.querySelectorAll('table tbody tr');
    expect(rows.length).toEqual(nextGamesPage.edges.length);
    expect(pageButtons[0].nativeElement.disabled).toBeTrue();
    expect(pageButtons[1].nativeElement.disabled).toBeFalse();
  });
});
