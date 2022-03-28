import { TestBed } from '@angular/core/testing';

import { LeagueService } from './league.service';
import { LEAGUE_WITH_PLAYERS, LEAGUE_WITH_PLAYERS_AND_GAMES, LEAGUES_PAGE } from '../../../testing/data';
import { ApolloTestingController, ApolloTestingModule } from 'apollo-angular/testing';

describe('LeagueService', () => {
  let service: LeagueService;
  let controller: ApolloTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ ApolloTestingModule ]
    });
    controller = TestBed.inject(ApolloTestingController)
    service = TestBed.inject(LeagueService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should return leagues page', () => {
    service.leagues(2).subscribe(response => {
      expect(response.data.leagues.pageInfo.hasNextPage).toEqual(LEAGUES_PAGE.pageInfo.hasNextPage);
      expect(response.data.leagues.pageInfo.endCursor).toEqual(LEAGUES_PAGE.pageInfo.endCursor);
      expect(response.data.leagues.edges.length).toEqual(LEAGUES_PAGE.edges.length);
    });
    const operation = controller.expectOne('leagues');
    operation.flush({data: {leagues: LEAGUES_PAGE}});
    expect(operation.operation.variables.first).toEqual(2);
    controller.verify();
  });

  it('should return leagues page after given cursor', () => {
    service.leagues(2, "league-3-cur").subscribe(response => {
      expect(response.data.leagues.pageInfo.hasNextPage).toEqual(LEAGUES_PAGE.pageInfo.hasNextPage);
      expect(response.data.leagues.pageInfo.endCursor).toEqual(LEAGUES_PAGE.pageInfo.endCursor);
      expect(response.data.leagues.edges.length).toEqual(LEAGUES_PAGE.edges.length);
    });
    const operation = controller.expectOne('leagues');
    operation.flush({data: {leagues: LEAGUES_PAGE}});
    expect(operation.operation.variables.first).toEqual(2);
    expect(operation.operation.variables.after).toEqual("league-3-cur");
    controller.verify();
  });

  it('should return league with players', () => {
    service.leagueWithPlayers(LEAGUE_WITH_PLAYERS.id).subscribe(({data}) => {
      expect(data.league.id).toEqual(LEAGUE_WITH_PLAYERS_AND_GAMES.id);
      expect(data.league.name).toEqual(LEAGUE_WITH_PLAYERS_AND_GAMES.name);
      expect(data.league.allowDraws).toEqual(LEAGUE_WITH_PLAYERS.allowDraws);
      expect(data.league.maxScore).toEqual(LEAGUE_WITH_PLAYERS.maxScore);
      expect(data.league.players.length).toEqual(LEAGUE_WITH_PLAYERS_AND_GAMES.players.length);
    });
    const operation = controller.expectOne('league');
    operation.flush({data: {league: LEAGUE_WITH_PLAYERS}});
    expect(operation.operation.variables.id).toEqual(LEAGUE_WITH_PLAYERS.id);
  });

  it('should return league with players, completed and scheduled games', () => {
    service.leagueWithPlayersAndGames(LEAGUE_WITH_PLAYERS_AND_GAMES.id).subscribe(({data}) => {
      expect(data.league.id).toEqual(LEAGUE_WITH_PLAYERS_AND_GAMES.id);
      expect(data.league.name).toEqual(LEAGUE_WITH_PLAYERS_AND_GAMES.name);
      expect(data.league.players.length).toEqual(LEAGUE_WITH_PLAYERS_AND_GAMES.players.length);
      expect(data.league.completedGames.pageInfo.hasNextPage).toEqual(LEAGUE_WITH_PLAYERS_AND_GAMES.completedGames.pageInfo.hasNextPage);
      expect(data.league.completedGames.pageInfo.endCursor).toEqual(LEAGUE_WITH_PLAYERS_AND_GAMES.completedGames.pageInfo.endCursor);
      expect(data.league.completedGames.edges.length).toEqual(LEAGUE_WITH_PLAYERS_AND_GAMES.completedGames.edges.length);
      expect(data.league.scheduledGames.pageInfo.hasNextPage).toEqual(LEAGUE_WITH_PLAYERS_AND_GAMES.scheduledGames.pageInfo.hasNextPage);
      expect(data.league.scheduledGames.pageInfo.endCursor).toEqual(LEAGUE_WITH_PLAYERS_AND_GAMES.scheduledGames.pageInfo.endCursor);
      expect(data.league.scheduledGames.edges.length).toEqual(LEAGUE_WITH_PLAYERS_AND_GAMES.scheduledGames.edges.length);
    });
    const operation = controller.expectOne('league');
    operation.flush({data: {league: LEAGUE_WITH_PLAYERS_AND_GAMES}});
    expect(operation.operation.variables.id).toEqual(LEAGUE_WITH_PLAYERS_AND_GAMES.id);
    expect(operation.operation.variables.firstCompletedGames).toEqual(5);
    expect(operation.operation.variables.firstScheduledGames).toEqual(5);
    controller.verify();
  });
});
