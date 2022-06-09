import { Injectable } from '@angular/core';
import { Apollo, gql } from 'apollo-angular';
import { Player } from '../../shared/model/player';
import { PLAYER_FIELDS } from '../../shared/graphql-fields';

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
}
