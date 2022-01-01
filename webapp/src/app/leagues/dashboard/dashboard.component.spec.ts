import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DashboardComponent } from './dashboard.component';
import { of } from 'rxjs';
import { ActivatedRoute } from '@angular/router';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { LeagueService } from '../shared/league.service';
import { ActivatedRouteStub } from '../../../testing/stubs';
import { LEAGUE_WITH_PLAYERS_AND_GAMES } from '../../../testing/data';
import { Component, Input } from '@angular/core';
import { Player } from '../../shared/model/player';
import { By } from '@angular/platform-browser';

describe('DashboardComponent', () => {
  let component: DashboardComponent;
  let fixture: ComponentFixture<DashboardComponent>;
  let activatedRouteStub: ActivatedRouteStub;
  let leagueServiceSpy = jasmine.createSpyObj('LeagueService', ['leagueWithPlayersAndGames']);

  beforeEach(async () => {
    activatedRouteStub = new ActivatedRouteStub();
    await TestBed.configureTestingModule({
      declarations: [ DashboardComponent, PlayerRankingComponentStub ],
      imports: [ MatProgressSpinnerModule ],
      providers: [
        { provide: ActivatedRoute, useValue: activatedRouteStub },
        { provide: LeagueService, useValue: leagueServiceSpy }
      ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    activatedRouteStub.setParamMap({league_id: 'league-1'});
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

  it('should contain player-ranking component', () => {
    const playerRankingComponent = fixture.debugElement.query(By.directive(PlayerRankingComponentStub));
    expect(playerRankingComponent).toBeTruthy();
  });
});

@Component({selector: 'app-player-ranking', template: ''})
class PlayerRankingComponentStub {
  @Input() players: Player[];
}
