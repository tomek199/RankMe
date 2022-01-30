export const PAGE_INFO_FIELDS = `
  pageInfo {
    hasNextPage endCursor
  }
`;

export const COMPLETED_GAME_EDGE_FIELDS = `
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
`;

export const SCHEDULED_GAME_EDGE_FIELDS = `
  edges {
    node {
      id dateTime
      playerOneId playerOneName playerOneRating
      playerTwoId playerTwoName playerTwoRating
    }
  }
`;
