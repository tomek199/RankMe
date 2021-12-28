import { Injectable } from '@angular/core';
import { Apollo, gql } from 'apollo-angular';
import { Observable } from 'rxjs';
import { ApolloQueryResult } from '@apollo/client';

@Injectable({
  providedIn: 'root'
})
export class VersionService {

  constructor(private apollo: Apollo) { }

  apiVersion(): Observable<ApolloQueryResult<any>> {
    return this.apollo.query<any>({
      query: gql`{version}`
    });
  }
}
