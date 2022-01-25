import { Injectable } from '@angular/core';
import { Apollo, gql } from 'apollo-angular';
import { League } from '../../shared/model/league';
import { Page } from '../../shared/model/page';

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
    return this.apollo.query<{leagues: Page<League>}>({
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

  leagueWithPlayersAndGames(id: string, firstGames: number = 5) {
    return this.apollo.query<{league: League}>({
      query: gql`
        query league($id: String!, $firstGames: Int!) {
          league(query: {id: $id}) {
            id name
            players {
              id name rating
            }
            games(first: $firstGames) {
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
        }
      `,
      variables: {
        id: id,
        firstGames: firstGames
      }
    });
  }
}
