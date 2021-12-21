import { TestBed } from '@angular/core/testing';

import { VersionService } from './version.service';
import { Apollo } from 'apollo-angular';
import { of } from 'rxjs';
import { ApolloQueryResult } from '@apollo/client';

describe('InfoService', () => {
  let service: VersionService;
  let apolloSpy = jasmine.createSpyObj('Apollo', ['query']);

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        { provide: Apollo, useValue: apolloSpy }
      ]
    });
    service = TestBed.inject(VersionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should return API version', () => {
    apolloSpy.query.and.returnValue(of({data: {version: '1.2.3'}} as ApolloQueryResult<any>));
    service.apiVersion().subscribe(({data}) => {
      expect(data.version).toEqual('1.2.3')
    });
  });
});
