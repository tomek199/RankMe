import { TestBed } from '@angular/core/testing';

import { LeagueService } from './league.service';
import { Apollo } from 'apollo-angular';
import { of } from 'rxjs';
import { ApolloQueryResult } from '@apollo/client';
import { LeaguesPage } from '../../shared/model/leagues-page';
import { Page } from '../../shared/model/page';
import { League } from '../../shared/model/league';

describe('LeagueService', () => {
  let service: LeagueService;
  let apolloSpy = jasmine.createSpyObj('Apollo', ['query'])
  const leaguesPage = {
    pageInfo: {
      hasPreviousPage: false, hasNextPage: true, startCursor: 'a1a1a1', endCursor: 'b2b2b2'
    },
    edges: [
      {
        cursor: 'a1a1a1',
        node: {
          id: 'aaa111', name: 'League-aaa111'
        }
      },
      {
        cursor: 'b2b2b2',
        node: {
          id: 'bbb222', name: 'League-bbb222'
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
    apolloSpy.query.and.returnValue(of({data: {leagues: leaguesPage}} as ApolloQueryResult<LeaguesPage>));
    service.leagues(2).subscribe(({data}) => {
      expect(data.leagues.pageInfo).toEqual(leaguesPage.pageInfo);
      expect(data.leagues.edges).toEqual(leaguesPage.edges)
    })
  });

  it('should return leagues page after given cursor', () => {
    apolloSpy.query.and.returnValue(of({data: {leagues: leaguesPage}} as ApolloQueryResult<LeaguesPage>));
    service.leagues(2, "c1c1c1").subscribe(({data}) => {
      expect(data.leagues.pageInfo).toEqual(leaguesPage.pageInfo);
      expect(data.leagues.edges).toEqual(leaguesPage.edges)
    })
  });
});
