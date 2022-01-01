import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DashboardComponent } from './dashboard.component';
import { of, ReplaySubject } from 'rxjs';
import { ActivatedRoute, convertToParamMap, ParamMap, Params } from '@angular/router';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { LeagueService } from '../shared/league.service';
import { League } from '../../shared/model/league';

describe('DashboardComponent', () => {
  let component: DashboardComponent;
  let fixture: ComponentFixture<DashboardComponent>;
  let activatedRouteStub: ActivatedRouteStub;
  let leagueServiceSpy = jasmine.createSpyObj('LeagueService', ['leagueWithPlayersAndGames']);
  const league = {
    id: 'league-1', name: "League-1",
    players: [
      {id: 'player-1', name: 'Player-1', rating: 2367},
      {id: 'player-2', name: 'Player-2', rating: 1594}
    ],
    games: {
      pageInfo: {
        endCursor: 'game-1-cur',
        hasNextPage: false
      },
      edges: [
        {
          cursor: 'game-1-cur',
          node: {
            id: 'game-1', dateTime: '2021-12-30T15:38:05',
            playerOneId: 'player-1', playerOneName: 'Player-1', playerOneRating: 2367,
            playerTwoId: 'player-2', playerTwoName: 'Player-2', playerTwoRating: 1594,
            result: {
              playerOneScore: 2, playerOneRatingDelta: 67,
              playerTwoScore: 1, playerTwoRatingDelta: -67
            }
          }
        }
      ]
    }
  } as League;

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
    leagueServiceSpy.leagueWithPlayersAndGames.and.returnValue(of({data: {league: league}}));
    fixture = TestBed.createComponent(DashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should get league data from API', () => {
    expect(fixture.componentInstance.isLoading).toBeFalse();
    expect(fixture.componentInstance.league).toEqual(league);
  });
});

class ActivatedRouteStub {
  private subject = new ReplaySubject<ParamMap>();

  constructor(initialParams?: Params) {
    this.setParamMap(initialParams);
  }

  readonly params = this.subject.asObservable();

  setParamMap(params: Params = {}) {
    this.subject.next(convertToParamMap(params));
  }
}
