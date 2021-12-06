import { TestBed } from '@angular/core/testing';

import { InfoService } from './info.service';
import { Apollo } from 'apollo-angular';
import { of } from 'rxjs';
import { ApolloQueryResult } from '@apollo/client';

describe('InfoService', () => {
  let service: InfoService;
  let apolloSpy = jasmine.createSpyObj('Apollo', ['query']);

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        { provide: Apollo, useValue: apolloSpy }
      ]
    });
    service = TestBed.inject(InfoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should return API version', () => {
    apolloSpy.query.and.returnValue(of({data: {info: 'API 1.2.3'}} as ApolloQueryResult<any>));
    service.apiVersion().subscribe(({data}) => {
      expect(data.info).toEqual('API 1.2.3')
    });
  });
});
