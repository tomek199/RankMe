import { TestBed } from '@angular/core/testing';

import { GameService } from './game.service';
import { Apollo } from 'apollo-angular';
import { of } from 'rxjs';
import { GAMES_PAGE } from '../../../testing/data';

describe('GameService', () => {
  let service: GameService;
  let apolloSpy = jasmine.createSpyObj('Apollo', ['query'])

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        { provide: Apollo, useValue: apolloSpy }
      ]
    });
    service = TestBed.inject(GameService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should return games page', () => {
    apolloSpy.query.and.returnValue(of({data: {games: GAMES_PAGE}}));
    service.games('league-1', 3).subscribe(({data}) => {
      expect(data.games.pageInfo).toEqual(GAMES_PAGE.pageInfo);
      expect(data.games.edges).toEqual(GAMES_PAGE.edges);
    });
  });

  it('should return games page after given cursor', () => {
    apolloSpy.query.and.returnValue(of({data: {games: GAMES_PAGE}}));
    service.games('league-1', 3).subscribe(({data}) => {
      expect(data.games.pageInfo).toEqual(GAMES_PAGE.pageInfo);
      expect(data.games.edges).toEqual(GAMES_PAGE.edges);
    });
  });
});
