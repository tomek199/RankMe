import { TestBed } from '@angular/core/testing';

import { GameService } from './game.service';
import { GAMES_PAGE } from '../../../testing/data';
import { ApolloTestingController, ApolloTestingModule } from 'apollo-angular/testing';

describe('GameService', () => {
  let service: GameService;
  let controller: ApolloTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ ApolloTestingModule ]
    });
    service = TestBed.inject(GameService);
    controller = TestBed.inject(ApolloTestingController);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should return games page', () => {
    service.games('league-1', 3).subscribe(({data}) => {
      expect(data.games.pageInfo.hasNextPage).toEqual(GAMES_PAGE.pageInfo.hasNextPage);
      expect(data.games.pageInfo.endCursor).toEqual(GAMES_PAGE.pageInfo.endCursor);
      expect(data.games.edges.length).toEqual(GAMES_PAGE.edges.length);
    });
    const operation = controller.expectOne('games');
    operation.flush({data: {games: GAMES_PAGE}});
    expect(operation.operation.variables.leagueId).toEqual('league-1');
    expect(operation.operation.variables.first).toEqual(3);
    controller.verify();
  });

  it('should return games page after given cursor', () => {
    service.games('league-1', 3, 'game-5').subscribe(({data}) => {
      expect(data.games.pageInfo.hasNextPage).toEqual(GAMES_PAGE.pageInfo.hasNextPage);
      expect(data.games.pageInfo.endCursor).toEqual(GAMES_PAGE.pageInfo.endCursor);
      expect(data.games.edges.length).toEqual(GAMES_PAGE.edges.length);
    });
    const operation = controller.expectOne('games');
    operation.flush({data: {games: GAMES_PAGE}});
    expect(operation.operation.variables.leagueId).toEqual('league-1');
    expect(operation.operation.variables.first).toEqual(3);
    expect(operation.operation.variables.after).toEqual('game-5');
    controller.verify();
  });
});
