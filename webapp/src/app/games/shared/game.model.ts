export class PlayGameCommand {
  constructor(
    public playerOneId: string,
    public playerTwoId: string,
    public playerOneScore: number,
    public playerTwoScore: number
  ) { }
}
