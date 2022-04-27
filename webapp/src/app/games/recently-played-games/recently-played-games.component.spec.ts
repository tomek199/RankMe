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
import { SnackbarService } from '../../shared/snackbar/snackbar.service';
import { SnackbarServiceStub } from '../../../testing/stubs';
import { MatDialog } from '@angular/material/dialog';
import { Page } from '../../shared/model/page';
import { CompletedGame } from '../../shared/model/game';

describe('RecentlyPlayedGamesComponent', () => {
  let component: RecentlyPlayedGames;
  let fixture: ComponentFixture<RecentlyPlayedGames>;
  let gameServiceSpy = jasmine.createSpyObj('GameService', ['completedGamesAfter', 'completedGamesBefore']);
  let matDialogSpy = jasmine.createSpyObj('MatDialog', ['open']);

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RecentlyPlayedGames ],
      imports: [ MatCardModule, MatTableModule, MatIconModule, MatButtonModule, MatProgressBarModule ],
      providers: [
        { provide: GameService, useValue: gameServiceSpy },
        { provide: SnackbarService, useClass: SnackbarServiceStub },
        { provide: MatDialog, useValue: matDialogSpy }
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

  it('should show "No data" row when there is no completed games', () => {
    component.gamesPage = {
      pageInfo: {hasPreviousPage: false, hasNextPage: false, startCursor: null, endCursor: null},
      edges: []
    }
    fixture.detectChanges();
    const noDataRow = fixture.nativeElement.querySelector('table tbody tr td');
    expect(noDataRow.textContent).toEqual("No data");
  });

  it('should show completed games in table', () => {
    const rows = fixture.nativeElement.querySelectorAll('table tbody tr');
    rows.forEach((row: Element, index: number) => {
      const columns = row.querySelectorAll('td');
      const datetime = columns[0].querySelectorAll('span');
      expect(datetime[0].textContent).toBeDefined();
      expect(datetime[1].textContent).toEqual(LEAGUE_WITH_PLAYERS_AND_GAMES.completedGames.edges[index].node.dateTime.substring(11, 16))
      const players = columns[1].querySelectorAll('span');
      expect(players[0].textContent).toEqual(LEAGUE_WITH_PLAYERS_AND_GAMES.completedGames.edges[index].node.playerOneName);
      expect(players[1].textContent).toEqual(LEAGUE_WITH_PLAYERS_AND_GAMES.completedGames.edges[index].node.playerTwoName);
      const result = columns[2].querySelectorAll('span');
      expect(result[0].textContent).toEqual(LEAGUE_WITH_PLAYERS_AND_GAMES.completedGames.edges[index].node.result.playerOneScore.toString())
      expect(result[1].textContent).toEqual(LEAGUE_WITH_PLAYERS_AND_GAMES.completedGames.edges[index].node.result.playerTwoScore.toString());
    })
  });

  it('should load next completed games page after button click', () => {
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
            playerTwoId: 'player-2', playerTwoName: 'Player-2', playerTwoRating: 1600,
            result: {
              playerOneScore: 2, playerOneRatingDelta: 67, playerTwoScore: 1, playerTwoRatingDelta: -67
            }
          }
        }
      ]
    } as Page<CompletedGame>
    gameServiceSpy.completedGamesAfter.and.returnValue(of({data: { completedGames: nextGamesPage }}));
    const pageButtons = fixture.debugElement.queryAll(By.css('button.mat-raised-button'));
    pageButtons[1].triggerEventHandler('click', null)
    fixture.detectChanges()
    const rows = fixture.nativeElement.querySelectorAll('table tbody tr');
    expect(rows.length).toEqual(nextGamesPage.edges.length);
    expect(pageButtons[0].nativeElement.disabled).toBeFalse();
    expect(pageButtons[1].nativeElement.disabled).toBeTrue();
  });

  it('should load previous completed games page after button click', () => {
    const previousGamesPage = {
      pageInfo: {
        hasPreviousPage: false, hasNextPage: true, startCursor: 'game-1-cur', endCursor: 'game-1-cur'
      },
      edges: [
        {
          cursor: 'game-1-cur',
          node: {
            id: 'game-1', dateTime: '2021-12-30T15:38:05',
            playerOneId: 'player-1', playerOneName: 'Player-1', playerOneRating: 2300,
            playerTwoId: 'player-2', playerTwoName: 'Player-2', playerTwoRating: 1600,
            result: {
              playerOneScore: 2, playerOneRatingDelta: 67, playerTwoScore: 1, playerTwoRatingDelta: -67
            }
          }
        }
      ]
    } as Page<CompletedGame>
    gameServiceSpy.completedGamesBefore.and.returnValue(of({data: { completedGames: previousGamesPage }}));
    const pageButtons = fixture.debugElement.queryAll(By.css('button.mat-raised-button'));
    pageButtons[0].triggerEventHandler('click', null)
    fixture.detectChanges()
    const rows = fixture.nativeElement.querySelectorAll('table tbody tr');
    expect(rows.length).toEqual(previousGamesPage.edges.length);
    expect(pageButtons[0].nativeElement.disabled).toBeTrue();
    expect(pageButtons[1].nativeElement.disabled).toBeFalse();
  });

  it('should emit event when new game is played', () => {
    spyOn(component.gamePlayed, 'emit');
    const matDialogRefSpy = jasmine.createSpyObj('MatDialog', ['afterClosed']);
    matDialogSpy.open.and.returnValue(matDialogRefSpy);
    matDialogRefSpy.afterClosed.and.returnValue(of(of(true)));
    const playGameButton = fixture.debugElement.query(By.css('button.mat-raised-button[color=primary]'));
    playGameButton.triggerEventHandler('click', null);
    fixture.detectChanges();
    expect(component.gamePlayed.emit).toHaveBeenCalledWith(true);
    expect(fixture.nativeElement.querySelectorAll('table tbody tr').length).toEqual(COMPLETED_GAMES_PAGE.edges.length);
  });

  it('should do nothing when play-game dialog is closed by cancel button', () => {
    const matDialogRefSpy = jasmine.createSpyObj('MatDialog', ['afterClosed']);
    matDialogSpy.open.and.returnValue(matDialogRefSpy);
    matDialogRefSpy.afterClosed.and.returnValue(of(null));
    const playGameButton = fixture.debugElement.query(By.css('button.mat-raised-button[color=primary]'));
    playGameButton.triggerEventHandler('click', null);
    fixture.detectChanges();
    expect(fixture.nativeElement.querySelectorAll('table tbody tr').length).toEqual(COMPLETED_GAMES_PAGE.edges.length);
  });
});
