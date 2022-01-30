import { Injectable } from '@angular/core';
import { Apollo, gql } from 'apollo-angular';
import { Page } from '../../shared/model/page';
import { CompletedGame } from '../../shared/model/game';
import { COMPLETED_GAME_EDGE_FIELDS, PAGE_INFO_FIELDS } from '../../shared/graphql-fields';

@Injectable({
  providedIn: 'root'
})
export class GameService {

  constructor(private apollo: Apollo) { }

  completedGames(leagueId: string, first: number, after?: string) {
    if (after) return this.firstCompletedGamesAfter(leagueId, first, after)
    else return this.firstCompletedGames(leagueId, first);
  }

  private firstCompletedGames(leagueId: string, first: number) {
    return this.apollo.query<{completedGames: Page<CompletedGame>}>({
      query: gql`
        query completedGames($leagueId: String!, $first: Int!) {
          completedGames(query: {leagueId: $leagueId, first: $first}) {
            ${PAGE_INFO_FIELDS}
            ${COMPLETED_GAME_EDGE_FIELDS}
          }
        }
      `,
      variables: {
        leagueId: leagueId,
        first: first
      }
    });
  }

  private firstCompletedGamesAfter(leagueId: string, first: number, after: string) {
    return this.apollo.query<{completedGames: Page<CompletedGame>}>({
      query: gql`
        query completedGames($leagueId: String!, $first: Int!, $after: String) {
          completedGames(query: {leagueId: $leagueId, first: $first, after: $after}) {
            ${PAGE_INFO_FIELDS}
            ${COMPLETED_GAME_EDGE_FIELDS}
          }
        }
      `,
      variables: {
        leagueId: leagueId,
        first: first,
        after: after
      }
    });
  }
}
