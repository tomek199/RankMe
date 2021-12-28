import { Injectable } from '@angular/core';
import { Apollo, gql } from 'apollo-angular';
import { LeaguesPage } from '../../shared/model/leagues-page';

@Injectable({
  providedIn: 'root'
})
export class LeagueService {
  constructor(private apollo: Apollo) { }

  leagues(first: number, after?: string) {
    if (after) return this.firstLeaguesAfter(first, after);
    else return this.firstLeagues(first)
  }

  private firstLeagues(first: number) {
    return this.apollo.query<LeaguesPage>({
      query: gql`
        query leagues($first: Int!) {
          leagues(query: {first: $first}) {
            pageInfo {
              hasNextPage endCursor
            }
            edges {
              node {
                id name
              }
            }
          }
        }
      `,
      variables: {
        first: first
      }
    });
  }

  private firstLeaguesAfter(first: number, after: string) {
    return this.apollo.query<LeaguesPage>({
      query: gql`
        query leagues($first: Int!, $after: String) {
          leagues(query: {first: $first, after: $after}) {
            pageInfo {
              hasNextPage endCursor
            }
            edges {
              node {
                id name
              }
            }
          }
        }
      `,
      variables: {
        first: first,
        after: after
      }
    });
  }
}
