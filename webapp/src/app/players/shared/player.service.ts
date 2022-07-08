import { Injectable } from '@angular/core';
import { Apollo, gql } from 'apollo-angular';
import { Player } from '../../shared/model/player';
import { PLAYER_FIELDS } from '../../shared/graphql-fields';
import { CreatePlayerCommand } from './player.model';

@Injectable({
  providedIn: 'root'
})
export class PlayerService {

  constructor(private apollo: Apollo) { }

  players(leagueId: string) {
    return this.apollo.query<{players: Player[]}>({
      query: gql`
        query players($leagueId: String!) {
          players(query: {leagueId: $leagueId}) {
            ${PLAYER_FIELDS}
          }
        }
      `,
      variables: {
        leagueId: leagueId
      }
    });
  }

  addPlayer(command: CreatePlayerCommand) {
    return this.apollo.mutate<{createPlayer: string}>({
      mutation: gql`
        mutation createPlayer($command: CreatePlayerCommand!) {
          createPlayer(command: $command)
        }
      `,
      variables: {
        command: command
      }
    });
  }
}
