import { Injectable } from '@angular/core';
import { Apollo, gql } from 'apollo-angular';
import { League } from '../../shared/model/league';
import { Page } from '../../shared/model/page';
import { COMPLETED_GAME_EDGE_FIELDS, PAGE_INFO_FIELDS, SCHEDULED_GAME_EDGE_FIELDS } from '../../shared/graphql-fields';

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
    return this.apollo.query<{leagues: Page<League>}>({
      query: gql`
        query leagues($first: Int!) {
          leagues(query: {first: $first}) {
            ${PAGE_INFO_FIELDS}
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
    return this.apollo.query<{leagues: Page<League>}>({
      query: gql`
        query leagues($first: Int!, $after: String) {
          leagues(query: {first: $first, after: $after}) {
            ${PAGE_INFO_FIELDS}
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

  leagueWithPlayersAndGames(id: string, firstCompletedGames: number = 5, firstScheduledGames: number = 5) {
    return this.apollo.query<{league: League}>({
      query: gql`
        query league($id: String!, $firstCompletedGames: Int!, $firstScheduledGames: Int!) {
          league(query: {id: $id}) {
            id name
            players {
              id name rating
            }
            completedGames(first: $firstCompletedGames) {
              ${PAGE_INFO_FIELDS}
              ${COMPLETED_GAME_EDGE_FIELDS}
            }
            scheduledGames(first: $firstScheduledGames) {
              ${PAGE_INFO_FIELDS}
              ${SCHEDULED_GAME_EDGE_FIELDS}
            }
          }
        }
      `,
      variables: {
        id: id,
        firstCompletedGames: firstCompletedGames,
        firstScheduledGames: firstScheduledGames
      }
    });
  }
}
