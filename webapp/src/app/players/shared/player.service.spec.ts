import { TestBed } from '@angular/core/testing';

import { PlayerService } from './player.service';
import { ApolloTestingController, ApolloTestingModule } from 'apollo-angular/testing';
import { LEAGUES_PAGE, PLAYERS, SUBMITTED } from '../../../testing/data';
import { CreatePlayerCommand } from './player.model';

describe('PlayerService', () => {
  let service: PlayerService;
  let controller: ApolloTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ ApolloTestingModule ]
    });
    controller = TestBed.inject(ApolloTestingController)
    service = TestBed.inject(PlayerService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should return players', () => {
    const leagueId = 'league-1'
    service.players(leagueId).subscribe(response => {
      expect(response.data.players).toEqual(PLAYERS);
    });
    const operation = controller.expectOne('players');
    operation.flush({data: {players: PLAYERS}});
    expect(operation.operation.variables.leagueId).toEqual(leagueId);
    controller.verify();
  });

  it ('should submit CreatePlayerCommand', () => {
    const command = new CreatePlayerCommand('league-1', 'Player 1');
    service.addPlayer(command).subscribe(({data}) => {
      expect(data?.createPlayer).toEqual(SUBMITTED);
    });
    const operation = controller.expectOne('createPlayer');
    operation.flush({data: {createPlayer: SUBMITTED}});
    expect(operation.operation.variables.command).toEqual(command);
    controller.verify();
  });

  it ('should subscribe PlayerCreated', () => {
    const leagueId = 'league-1';
    service.playerCreated(leagueId).subscribe(({data}) => {
      expect(data?.playerCreated).toEqual(PLAYERS[0]);
    });
    const operation = controller.expectOne('playerCreated');
    operation.flush({data: {playerCreated: PLAYERS[0]}});
    expect(operation.operation.variables.leagueId).toEqual(leagueId);
    controller.verify();
  });
});
