import { Player } from './player';
import { Page } from './page';
import { Game } from './game';

export interface League {
  id: string;
  name: string;
  allowDraws: boolean;
  maxScore: number;
  players: Player[];
  games: Page<Game>;
}
