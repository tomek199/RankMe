import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DashboardComponent } from './dashboard.component';
import { of } from 'rxjs';
import { ActivatedRoute } from '@angular/router';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { LeagueService } from '../shared/league.service';
import { League } from '../../shared/model/league';
import { ActivatedRouteStub } from '../../../testing/stubs';
import { LEAGUE_WITH_PLAYERS_AND_LEAGUE } from '../../../testing/data';

describe('DashboardComponent', () => {
  let component: DashboardComponent;
  let fixture: ComponentFixture<DashboardComponent>;
  let activatedRouteStub: ActivatedRouteStub;
  let leagueServiceSpy = jasmine.createSpyObj('LeagueService', ['leagueWithPlayersAndGames']);

  beforeEach(async () => {
    activatedRouteStub = new ActivatedRouteStub();
    await TestBed.configureTestingModule({
      declarations: [ DashboardComponent ],
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
    leagueServiceSpy.leagueWithPlayersAndGames.and.returnValue(of({data: {league: LEAGUE_WITH_PLAYERS_AND_LEAGUE}}));
    fixture = TestBed.createComponent(DashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should get league data from API', () => {
    expect(fixture.componentInstance.isLoading).toBeFalse();
    expect(fixture.componentInstance.league).toEqual(LEAGUE_WITH_PLAYERS_AND_LEAGUE);
  });
});
