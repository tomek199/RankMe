import { Injectable } from '@angular/core';
import { Apollo, gql } from 'apollo-angular';
import { Page } from '../../shared/model/page';
import { CompletedGame, ScheduledGame } from '../../shared/model/game';
import { COMPLETED_GAME_EDGE_FIELDS, PAGE_INFO_FIELDS, SCHEDULED_GAME_EDGE_FIELDS } from '../../shared/graphql-fields';
import { PlayGameCommand } from './game.model';

@Injectable({
  providedIn: 'root'
})
export class GameService {

  constructor(private apollo: Apollo) { }

  completedGames(leagueId: string, first: number) {
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

  completedGamesAfter(leagueId: string, first: number, after: string) {
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

  completedGamesBefore(leagueId: string, first: number, before: string) {
    return this.apollo.query<{completedGames: Page<CompletedGame>}>({
      query: gql`
        query completedGames($leagueId: String!, $first: Int!, $before: String) {
          completedGames(query: {leagueId: $leagueId, first: $first, before: $before}) {
            ${PAGE_INFO_FIELDS}
            ${COMPLETED_GAME_EDGE_FIELDS}
          }
        }
      `,
      variables: {
        leagueId: leagueId,
        first: first,
        before: before
      }
    });
  }

  scheduledGames(leagueId: string, first: number) {
    return this.apollo.query<{scheduledGames: Page<ScheduledGame>}>({
      query: gql`
        query scheduledGames($leagueId: String!, $first: Int!) {
          scheduledGames(query: {leagueId: $leagueId, first: $first}) {
            ${PAGE_INFO_FIELDS}
            ${SCHEDULED_GAME_EDGE_FIELDS}
          }
        }
      `,
      variables: {
        leagueId: leagueId,
        first: first
      }
    });
  }

  scheduledGamesAfter(leagueId: string, first: number, after: string) {
    return this.apollo.query<{scheduledGames: Page<ScheduledGame>}>({
      query: gql`
        query scheduledGames($leagueId: String!, $first: Int!, $after: String) {
          scheduledGames(query: {leagueId: $leagueId, first: $first, after: $after}) {
            ${PAGE_INFO_FIELDS}
            ${SCHEDULED_GAME_EDGE_FIELDS}
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

  scheduledGamesBefore(leagueId: string, first: number, before: string) {
    return this.apollo.query<{scheduledGames: Page<ScheduledGame>}>({
      query: gql`
        query scheduledGames($leagueId: String!, $first: Int!, $before: String) {
          scheduledGames(query: {leagueId: $leagueId, first: $first, before: $before}) {
            ${PAGE_INFO_FIELDS}
            ${SCHEDULED_GAME_EDGE_FIELDS}
          }
        }
      `,
      variables: {
        leagueId: leagueId,
        first: first,
        before: before
      }
    });
  }

  playGame(command: PlayGameCommand) {
    return this.apollo.mutate<{playGame: string}>({
      mutation: gql`
        mutation playGame($command: PlayGameCommand!) {
          playGame(command: $command)
        }
      `,
      variables: {
        command: command
      }
    });
  }
}
