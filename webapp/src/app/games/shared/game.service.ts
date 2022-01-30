import { Injectable } from '@angular/core';
import { Apollo, gql } from 'apollo-angular';
import { Page } from '../../shared/model/page';
import { CompletedGame } from '../../shared/model/game';

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
            pageInfo {
              hasNextPage endCursor
            }
            edges {
              node {
                id dateTime
                playerOneId playerOneName playerOneRating
                playerTwoId playerTwoName playerTwoRating
                result {
                  playerOneScore playerOneRatingDelta
                  playerTwoScore playerTwoRatingDelta
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

  private firstCompletedGamesAfter(leagueId: string, first: number, after: string) {
    return this.apollo.query<{completedGames: Page<CompletedGame>}>({
      query: gql`
        query completedGames($leagueId: String!, $first: Int!, $after: String) {
          completedGames(query: {leagueId: $leagueId, first: $first, after: $after}) {
            pageInfo {
              hasNextPage endCursor
            }
            edges {
              node {
                id dateTime
                playerOneId playerOneName playerOneRating
                playerTwoId playerTwoName playerTwoRating
                result {
                  playerOneScore playerOneRatingDelta
                  playerTwoScore playerTwoRatingDelta
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
