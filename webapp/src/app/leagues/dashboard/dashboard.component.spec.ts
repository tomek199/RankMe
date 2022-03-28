import { ComponentFixture, inject, TestBed } from '@angular/core/testing';

import { DashboardComponent } from './dashboard.component';
import { of } from 'rxjs';
import { ActivatedRoute, Router } from '@angular/router';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { LeagueService } from '../shared/league.service';
import { ActivatedRouteStub, SnackbarServiceStub } from '../../../testing/stubs';
import { LEAGUE_WITH_PLAYERS_AND_GAMES, LEAGUES_PAGE } from '../../../testing/data';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Player } from '../../shared/model/player';
import { By } from '@angular/platform-browser';
import { Page } from '../../shared/model/page';
import { Game } from '../../shared/model/game';
import { SnackbarService } from '../../shared/snackbar/snackbar.service';
import { RouterTestingModule } from '@angular/router/testing';

describe('DashboardComponent', () => {
  let component: DashboardComponent;
  let fixture: ComponentFixture<DashboardComponent>;
  let activatedRouteStub: ActivatedRouteStub;
  let leagueServiceSpy = jasmine.createSpyObj('LeagueService', ['leagueWithPlayersAndGames']);

  beforeEach(async () => {
    activatedRouteStub = new ActivatedRouteStub();
    await TestBed.configureTestingModule({
      declarations: [
        DashboardComponent,
        PlayerRankingComponentStub,
        RecentlyPlayedGamesComponentStub,
        ScheduledGamesComponentStub
      ],
      imports: [ RouterTestingModule, MatProgressSpinnerModule ],
      providers: [
        { provide: ActivatedRoute, useValue: activatedRouteStub },
        { provide: LeagueService, useValue: leagueServiceSpy },
        { provide: SnackbarService, useClass: SnackbarServiceStub }
      ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    activatedRouteStub.testParams = {league_id: LEAGUE_WITH_PLAYERS_AND_GAMES.id};
    leagueServiceSpy.leagueWithPlayersAndGames.and.returnValue(of({data: {league: LEAGUE_WITH_PLAYERS_AND_GAMES}}));
    fixture = TestBed.createComponent(DashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should get league data from API', () => {
    expect(fixture.componentInstance.isLoading).toBeFalse();
    expect(fixture.componentInstance.league).toEqual(LEAGUE_WITH_PLAYERS_AND_GAMES);
  });

  it('should redirect to leagues list when league does not exist', inject([Router], (router: Router) => {
    spyOn(router, 'navigate').and.stub();
    leagueServiceSpy.leagueWithPlayersAndGames.and.returnValue(of({data: {league: null}}));
    component.ngOnInit();
    expect(router.navigate).toHaveBeenCalledWith(['/leagues'])
  }));

  it('should contain player-ranking component', () => {
    const playerRankingComponent = fixture.debugElement.query(By.directive(PlayerRankingComponentStub));
    expect(playerRankingComponent).toBeTruthy();
  });

  it('should contain recently-played-games component', () => {
    const recentlyPlayedGamesComponent = fixture.debugElement.query(By.directive(RecentlyPlayedGamesComponentStub));
    expect(recentlyPlayedGamesComponent).toBeTruthy();
  });

  it('should contain scheduled-games component', () => {
    const scheduledGamesComponent = fixture.debugElement.query(By.directive(ScheduledGamesComponentStub));
    expect(scheduledGamesComponent).toBeTruthy();
  });
});

@Component({selector: 'app-player-ranking', template: ''})
class PlayerRankingComponentStub {
  @Input() players: Player[];
}

@Component({selector: 'app-recently-played-games', template: ''})
class RecentlyPlayedGamesComponentStub {
  @Input() leagueId: string;
  @Input() set gamesPage(gamesPage: Page<Game>) { }
  @Output() gamePlayed = new EventEmitter<boolean>();
}

@Component({selector: 'app-scheduled-games', template: ''})
class ScheduledGamesComponentStub {
  @Input() leagueId: string;
  @Input() set gamesPage(gamesPage: Page<Game>) { }
}
