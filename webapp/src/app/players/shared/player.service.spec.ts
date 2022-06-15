import { TestBed } from '@angular/core/testing';

import { PlayerService } from './player.service';
import { ApolloTestingController, ApolloTestingModule } from 'apollo-angular/testing';
import { LEAGUES_PAGE, PLAYERS } from '../../../testing/data';

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
});
