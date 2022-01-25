import { Injectable } from '@angular/core';
import { Apollo, gql } from 'apollo-angular';
import { Page } from '../../shared/model/page';
import { Game } from '../../shared/model/game';

@Injectable({
  providedIn: 'root'
})
export class GameService {

  constructor(private apollo: Apollo) { }

  games(leagueId: string, first: number, after?: string) {
    if (after) return this.firstGamesAfter(leagueId, first, after)
    else return this.firstGames(leagueId, first);
  }

  private firstGames(leagueId: string, first: number) {
    return this.apollo.query<{ games: Page<Game> }>({
      query: gql`
        query games($leagueId: String!, $first: Int!) {
          games(query: {leagueId: $leagueId, first: $first}) {
            pageInfo {
              hasNextPage endCursor
            }
            edges {
              node {
                id dateTime
                playerOneId playerOneName playerOneRating
                playerTwoId playerTwoName playerTwoRating
                ... on CompletedGame {
                  result {
                    playerOneScore playerOneRatingDelta
                    playerTwoScore playerTwoRatingDelta
                  }
                }
              }
            }
          }
        }
      `,
      variables: {
        leagueId: leagueId,
        first: first
      }
    });
  }

  private firstGamesAfter(leagueId: string, first: number, after: string) {
    return this.apollo.query<{ games: Page<Game> }>({
      query: gql`
        query games($leagueId: String!, $first: Int!, $after: String) {
          games(query: {leagueId: $leagueId, first: $first, after: $after}) {
            pageInfo {
              hasNextPage endCursor
            }
            edges {
              node {
                id dateTime
                playerOneId playerOneName playerOneRating
                playerTwoId playerTwoName playerTwoRating
                ... on CompletedGame {
                  result {
                    playerOneScore playerOneRatingDelta
                    playerTwoScore playerTwoRatingDelta
                  }
                }
              }
            }
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
