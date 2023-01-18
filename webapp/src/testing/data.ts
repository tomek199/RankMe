import { Page } from '../app/shared/model/page';
import { League } from '../app/shared/model/league';
import { CompletedGame, ScheduledGame } from '../app/shared/model/game';
import { Player } from '../app/shared/model/player';

export const SUBMITTED = 'SUBMITTED';

export const LEAGUES_PAGE = {
  pageInfo: {
    hasPreviousPage: true, hasNextPage: true, startCursor: 'league-1-cur', endCursor: 'league-2-cur'
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
    },
    {
      cursor: 'league-3-cur',
      node: {
        id: 'league-3', name: 'League-3'
      }
    }
  ]
} as Page<League>

export const COMPLETED_GAMES_PAGE = {
  pageInfo: {
    hasPreviousPage: true, hasNextPage: true, startCursor: 'game-1-cur', endCursor: 'game-3-cur'
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
          playerOneScore: 1, playerOneRatingDelta: 45,
          playerTwoScore: 0, playerTwoRatingDelta: -45
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
} as Page<CompletedGame>

export const SCHEDULED_GAMES_PAGE = {
  pageInfo: {
    hasPreviousPage: true, hasNextPage: true, startCursor: 'game-4-cur', endCursor: 'game-6-cur'
  },
  edges: [
    {
      cursor: 'game-4-cur',
      node: {
        id: 'game-4', dateTime: '2021-12-31T13:18:05',
        playerOneId: 'player-1', playerOneName: 'Player-1', playerOneRating: 2300,
        playerTwoId: 'player-2', playerTwoName: 'Player-2', playerTwoRating: 1600
      }
    },
    {
      cursor: 'game-5-cur',
      node: {
        id: 'game-5', dateTime: '2021-12-31T14:50:00',
        playerOneId: 'player-2', playerOneName: 'Player-2', playerOneRating: 2367,
        playerTwoId: 'player-3', playerTwoName: 'Player-3', playerTwoRating: 1594
      }
    },
    {
      cursor: 'game-6-cur',
      node: {
        id: 'game-6', dateTime: '2021-12-31T17:20:00',
        playerOneId: 'player-3', playerOneName: 'Player-3', playerOneRating: 2210,
        playerTwoId: 'player-1', playerTwoName: 'Player-1', playerTwoRating: 1865
      }
    }
  ]
} as Page<ScheduledGame>

export const PLAYERS = [
  {id: 'player-1', name: 'Player-1', deviation: 187, rating: 2367},
  {id: 'player-2', name: 'Player-2', deviation: 293, rating: 1594},
  {id: 'player-3', name: 'Player-3', deviation: 317, rating: 1863}
] as Player[];

export const LEAGUE = {
  id: 'league-1', name: 'League-1', allowDraws: true, maxScore: 5
} as League;

export const LEAGUE_WITH_PLAYERS = {
  id: 'league-1', name: 'League-1', allowDraws: false, maxScore: 3,
  players: PLAYERS
} as League;

export const LEAGUE_WITH_PLAYERS_AND_GAMES = {
  id: 'league-1', name: 'League-1',
  players: PLAYERS,
  completedGames: COMPLETED_GAMES_PAGE,
  scheduledGames: SCHEDULED_GAMES_PAGE
} as League;
