import { TestBed } from '@angular/core/testing';

import { LeagueService } from './league.service';
import { Apollo } from 'apollo-angular';
import { of } from 'rxjs';
import { ApolloQueryResult } from '@apollo/client';
import { Page } from '../../shared/model/page';
import { League } from '../../shared/model/league';

describe('LeagueService', () => {
  let service: LeagueService;
  let apolloSpy = jasmine.createSpyObj('Apollo', ['query'])
  const leaguesPage = {
    pageInfo: {
      hasPreviousPage: false, hasNextPage: true, startCursor: 'league-1-cur', endCursor: 'league-2-cur'
    },
    edges: [
      {
        cursor: 'league-1-cur',
        node: {
          id: 'league-1', name: 'League-1'
        }
      },
      {
        cursor: 'league-2-cur',
        node: {
          id: 'league-2', name: 'League-2'
        }
      }
    ]
  } as Page<League>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        { provide: Apollo, useValue: apolloSpy }
      ]
    });
    service = TestBed.inject(LeagueService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should return leagues page', () => {
    apolloSpy.query.and.returnValue(of({data: {leagues: leaguesPage}} as ApolloQueryResult<{leagues: Page<League>}>));
    service.leagues(2).subscribe(({data}) => {
      expect(data.leagues.pageInfo).toEqual(leaguesPage.pageInfo);
      expect(data.leagues.edges).toEqual(leaguesPage.edges)
    })
  });

  it('should return leagues page after given cursor', () => {
    apolloSpy.query.and.returnValue(of({data: {leagues: leaguesPage}} as ApolloQueryResult<{leagues: Page<League>}>));
    service.leagues(2, "league-3-cur").subscribe(({data}) => {
      expect(data.leagues.pageInfo).toEqual(leaguesPage.pageInfo);
      expect(data.leagues.edges).toEqual(leaguesPage.edges)
    })
  });

  it('should return league with players and games', () => {
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
    apolloSpy.query.and.returnValue(of({data: {league: league}} as ApolloQueryResult<{league: League}>));
    service.leagueWithPlayersAndGames(league.id).subscribe(({data}) => {
      expect(data.league.id).toEqual(league.id);
      expect(data.league.name).toEqual(league.name);
      expect(data.league.players).toEqual(data.league.players)
      expect(data.league.games.pageInfo).toEqual(league.games.pageInfo)
      expect(data.league.games.edges).toEqual(league.games.edges)
    })
  });
});
