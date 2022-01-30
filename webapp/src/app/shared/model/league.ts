import { Player } from './player';
import { Page } from './page';
import { CompletedGame, ScheduledGame } from './game';

export interface League {
  id: string;
  name: string;
  allowDraws: boolean;
  maxScore: number;
  players: Player[];
  completedGames: Page<CompletedGame>;
  scheduledGames: Page<ScheduledGame>;
}
