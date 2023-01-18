import { Injectable } from '@angular/core';
import { Apollo, gql } from 'apollo-angular';
import { League } from '../../shared/model/league';
import { Page } from '../../shared/model/page';
import { COMPLETED_GAME_EDGE_FIELDS, PAGE_INFO_FIELDS, SCHEDULED_GAME_EDGE_FIELDS } from '../../shared/graphql-fields';
import { CreateLeagueCommand } from './league.model';

@Injectable({
  providedIn: 'root'
})
export class LeagueService {
  constructor(private apollo: Apollo) { }

  public leagues(first: number) {
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

  public leaguesAfter(first: number, after: string) {
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

  public leaguesBefore(first: number, before: string) {
    return this.apollo.query<{leagues: Page<League>}>({
      query: gql`
        query leagues($first: Int!, $before: String) {
          leagues(query: {first: $first, before: $before}) {
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
        before: before
      }
    });
  }

  leagueWithPlayers(id: string) {
    return this.apollo.query<{league: League}>({
      query: gql`
        query league($id: String!) {
          league(query: {id: $id}) {
            id name allowDraws maxScore
            players {
              id name rating
            }
          }
        }
      `,
      variables: {
        id: id
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

  createLeague(command: CreateLeagueCommand) {
    return this.apollo.mutate<{createLeague: string}>({
      mutation: gql`
        mutation createLeague($command: CreateLeagueCommand!) {
          createLeague(command: $command)
        }
      `,
      variables: {
        command: command
      }
    });
  }

  leagueCreated(name: string) {
    return this.apollo.subscribe<{leagueCreated: League}>({
      query: gql`
        subscription leagueCreated($name: String!) {
          leagueCreated(name: $name) {
            id name
          }
        }
      `,
      variables: {
        name: name
      }
    });
  }
}
