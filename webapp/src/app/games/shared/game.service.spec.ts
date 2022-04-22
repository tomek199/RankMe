import { TestBed } from '@angular/core/testing';

import { GameService } from './game.service';
import { ApolloTestingController, ApolloTestingModule } from 'apollo-angular/testing';
import { COMPLETED_GAMES_PAGE, SCHEDULED_GAMES_PAGE } from '../../../testing/data';
import { PlayGameCommand } from './game.model';

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

  it('should return completed games page', () => {
    service.completedGames('league-1', 3).subscribe(({data}) => {
      expect(data.completedGames.pageInfo.hasPreviousPage).toEqual(COMPLETED_GAMES_PAGE.pageInfo.hasPreviousPage);
      expect(data.completedGames.pageInfo.hasNextPage).toEqual(COMPLETED_GAMES_PAGE.pageInfo.hasNextPage);
      expect(data.completedGames.pageInfo.startCursor).toEqual(COMPLETED_GAMES_PAGE.pageInfo.startCursor);
      expect(data.completedGames.pageInfo.endCursor).toEqual(COMPLETED_GAMES_PAGE.pageInfo.endCursor);
      expect(data.completedGames.edges.length).toEqual(COMPLETED_GAMES_PAGE.edges.length);
    });
    const operation = controller.expectOne('completedGames');
    operation.flush({data: {completedGames: COMPLETED_GAMES_PAGE}});
    expect(operation.operation.variables.leagueId).toEqual('league-1');
    expect(operation.operation.variables.first).toEqual(3);
    controller.verify();
  });

  it('should return completed games page after given cursor', () => {
    service.completedGamesAfter('league-1', 3, 'game-12').subscribe(({data}) => {
      expect(data.completedGames.pageInfo.hasPreviousPage).toEqual(COMPLETED_GAMES_PAGE.pageInfo.hasPreviousPage);
      expect(data.completedGames.pageInfo.hasNextPage).toEqual(COMPLETED_GAMES_PAGE.pageInfo.hasNextPage);
      expect(data.completedGames.pageInfo.startCursor).toEqual(COMPLETED_GAMES_PAGE.pageInfo.startCursor);
      expect(data.completedGames.pageInfo.endCursor).toEqual(COMPLETED_GAMES_PAGE.pageInfo.endCursor);
      expect(data.completedGames.edges.length).toEqual(COMPLETED_GAMES_PAGE.edges.length);
    });
    const operation = controller.expectOne('completedGamesAfter');
    operation.flush({data: {completedGames: COMPLETED_GAMES_PAGE}});
    expect(operation.operation.variables.leagueId).toEqual('league-1');
    expect(operation.operation.variables.first).toEqual(3);
    expect(operation.operation.variables.after).toEqual('game-12');
    controller.verify();
  });

  it('should return completed games page before given cursor', () => {
    service.completedGamesBefore('league-1', 3, 'game-15').subscribe(({data}) => {
      expect(data.completedGames.pageInfo.hasPreviousPage).toEqual(COMPLETED_GAMES_PAGE.pageInfo.hasPreviousPage);
      expect(data.completedGames.pageInfo.hasNextPage).toEqual(COMPLETED_GAMES_PAGE.pageInfo.hasNextPage);
      expect(data.completedGames.pageInfo.startCursor).toEqual(COMPLETED_GAMES_PAGE.pageInfo.startCursor);
      expect(data.completedGames.pageInfo.endCursor).toEqual(COMPLETED_GAMES_PAGE.pageInfo.endCursor);
      expect(data.completedGames.edges.length).toEqual(COMPLETED_GAMES_PAGE.edges.length);
    });
    const operation = controller.expectOne('completedGamesBefore');
    operation.flush({data: {completedGames: COMPLETED_GAMES_PAGE}});
    expect(operation.operation.variables.leagueId).toEqual('league-1');
    expect(operation.operation.variables.first).toEqual(3);
    expect(operation.operation.variables.after).toEqual('game-15');
    controller.verify();
  });

  it('should return scheduled games page', () => {
    service.scheduledGames('league-1', 3).subscribe(({data}) => {
      expect(data.scheduledGames.pageInfo.hasPreviousPage).toEqual(SCHEDULED_GAMES_PAGE.pageInfo.hasPreviousPage);
      expect(data.scheduledGames.pageInfo.hasNextPage).toEqual(SCHEDULED_GAMES_PAGE.pageInfo.hasNextPage);
      expect(data.scheduledGames.pageInfo.startCursor).toEqual(SCHEDULED_GAMES_PAGE.pageInfo.startCursor);
      expect(data.scheduledGames.pageInfo.endCursor).toEqual(SCHEDULED_GAMES_PAGE.pageInfo.endCursor);
      expect(data.scheduledGames.edges.length).toEqual(SCHEDULED_GAMES_PAGE.edges.length);
    });
    const operation = controller.expectOne('scheduledGames');
    operation.flush({data: {scheduledGames: SCHEDULED_GAMES_PAGE}});
    expect(operation.operation.variables.leagueId).toEqual('league-1');
    expect(operation.operation.variables.first).toEqual(3);
    controller.verify();
  });

  it('should return scheduled games page after given cursor', () => {
    service.scheduledGamesAfter('league-1', 3, 'game-12').subscribe(({data}) => {
      expect(data.scheduledGames.pageInfo.hasPreviousPage).toEqual(SCHEDULED_GAMES_PAGE.pageInfo.hasPreviousPage);
      expect(data.scheduledGames.pageInfo.hasNextPage).toEqual(SCHEDULED_GAMES_PAGE.pageInfo.hasNextPage);
      expect(data.scheduledGames.pageInfo.startCursor).toEqual(SCHEDULED_GAMES_PAGE.pageInfo.startCursor);
      expect(data.scheduledGames.pageInfo.endCursor).toEqual(SCHEDULED_GAMES_PAGE.pageInfo.endCursor);
      expect(data.scheduledGames.edges.length).toEqual(SCHEDULED_GAMES_PAGE.edges.length);
    });
    const operation = controller.expectOne('scheduledGamesAfter');
    operation.flush({data: {scheduledGames: SCHEDULED_GAMES_PAGE}});
    expect(operation.operation.variables.leagueId).toEqual('league-1');
    expect(operation.operation.variables.first).toEqual(3);
    expect(operation.operation.variables.after).toEqual('game-12');
    controller.verify();
  });

  it('should return scheduled games page before given cursor', () => {
    service.scheduledGamesBefore('league-1', 3, 'game-15').subscribe(({data}) => {
      expect(data.scheduledGames.pageInfo.hasPreviousPage).toEqual(SCHEDULED_GAMES_PAGE.pageInfo.hasPreviousPage);
      expect(data.scheduledGames.pageInfo.hasNextPage).toEqual(SCHEDULED_GAMES_PAGE.pageInfo.hasNextPage);
      expect(data.scheduledGames.pageInfo.startCursor).toEqual(SCHEDULED_GAMES_PAGE.pageInfo.startCursor);
      expect(data.scheduledGames.pageInfo.endCursor).toEqual(SCHEDULED_GAMES_PAGE.pageInfo.endCursor);
      expect(data.scheduledGames.edges.length).toEqual(SCHEDULED_GAMES_PAGE.edges.length);
    });
    const operation = controller.expectOne('scheduledGamesBefore');
    operation.flush({data: {scheduledGames: SCHEDULED_GAMES_PAGE}});
    expect(operation.operation.variables.leagueId).toEqual('league-1');
    expect(operation.operation.variables.first).toEqual(3);
    expect(operation.operation.variables.after).toEqual('game-15');
    controller.verify();
  });

  it('should submit PlayGameCommand', () => {
    const command = new PlayGameCommand('player-1', 'player-2', 3, 2);
    service.playGame(command).subscribe(({data}) => {
      expect(data?.playGame).toEqual('SUBMITTED');
    });
    const operation = controller.expectOne('playGame');
    operation.flush({data: {playGame: 'SUBMITTED'}});
    expect(operation.operation.variables.command).toEqual(command);
    controller.verify();
  });
});
