export interface Game {
  id: string;
  dateTime: string;
  playerOneId: string;
  playerOneName: string;
  playerOneRating: number;
  playerOneDeviation: number;
  playerTwoId: string;
  playerTwoName: string;
  playerTwoRating: number;
  playerTwoDeviation: number;
  result: Result | null
}

export interface Result {
  playerOneScore: number;
  playerOneDeviationDelta: number;
  playerOneRatingDelta: number;
  playerTwoScore: number;
  playerTwoDeviationDelta: number;
  playerTwoRatingDelta: number;
}
