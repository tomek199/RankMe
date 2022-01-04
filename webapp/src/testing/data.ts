import { Page } from '../app/shared/model/page';
import { League } from '../app/shared/model/league';
import { Game } from '../app/shared/model/game';

export const LEAGUES_PAGE = {
  pageInfo: {
    hasPreviousPage: false, hasNextPage: true, startCursor: 'league-1-cur', endCursor: 'league-2-cur'
  },
  edges: [
    {
      cursor: 'league-1-cur',
      node: {
        id: 'league-1', name: 'League-1'
      }
    },
    {
      cursor: 'league-2-cur',
      node: {
        id: 'league-2', name: 'League-2'
      }
    }
  ]
} as Page<League>

export const LEAGUE_WITH_PLAYERS_AND_GAMES = {
  id: 'league-1', name: "League-1",
  players: [
    {id: 'player-1', name: 'Player-1', rating: 2367},
    {id: 'player-2', name: 'Player-2', rating: 1594},
    {id: 'player-3', name: 'Player-3', rating: 1863},
  ],
  games: {
    pageInfo: {
      endCursor: 'game-2-cur',
      hasNextPage: false
    },
    edges: [
      {
        cursor: 'game-1-cur',
        node: {
          id: 'game-1', dateTime: '2021-12-30T15:38:05',
          playerOneId: 'player-1', playerOneName: 'Player-1', playerOneRating: 2300,
          playerTwoId: 'player-2', playerTwoName: 'Player-2', playerTwoRating: 1600,
          result: {
            playerOneScore: 2, playerOneRatingDelta: 67,
            playerTwoScore: 1, playerTwoRatingDelta: -67
          }
        }
      },
      {
        cursor: 'game-2-cur',
        node: {
          id: 'game-2', dateTime: '2021-12-30T15:50:00',
          playerOneId: 'player-2', playerOneName: 'Player-2', playerOneRating: 2367,
          playerTwoId: 'player-3', playerTwoName: 'Player-3', playerTwoRating: 1594,
          result: {
            playerOneScore: 2, playerOneRatingDelta: 34,
            playerTwoScore: 0, playerTwoRatingDelta: -34
          }
        }
      }
    ]
  }
} as League;

export const GAMES_PAGE = {
  pageInfo: {
    hasPreviousPage: false, hasNextPage: true, startCursor: 'game-1-cur', endCursor: 'game-3-cur'
  },
  edges: [
    {
      cursor: 'game-1-cur',
      node: {
        id: 'game-1', dateTime: '2021-12-30T15:38:05',
        playerOneId: 'player-1', playerOneName: 'Player-1', playerOneRating: 2300,
        playerTwoId: 'player-2', playerTwoName: 'Player-2', playerTwoRating: 1600,
        result: {
          playerOneScore: 2, playerOneRatingDelta: 67,
          playerTwoScore: 1, playerTwoRatingDelta: -67
        }
      }
    },
    {
      cursor: 'game-2-cur',
      node: {
        id: 'game-2', dateTime: '2021-12-30T15:50:00',
        playerOneId: 'player-2', playerOneName: 'Player-2', playerOneRating: 2367,
        playerTwoId: 'player-3', playerTwoName: 'Player-3', playerTwoRating: 1594,
        result: {
          playerOneScore: 2, playerOneRatingDelta: 34,
          playerTwoScore: 0, playerTwoRatingDelta: -34
        }
      }
    },
    {
      cursor: 'game-3-cur',
      node: {
        id: 'game-3', dateTime: '2021-12-30T16:20:00',
        playerOneId: 'player-3', playerOneName: 'Player-3', playerOneRating: 2210,
        playerTwoId: 'player-1', playerTwoName: 'Player-1', playerTwoRating: 1865,
        result: {
          playerOneScore: 2, playerOneRatingDelta: -34,
          playerTwoScore: 2, playerTwoRatingDelta: 34
        }
      }
    }
  ]
} as Page<Game>
