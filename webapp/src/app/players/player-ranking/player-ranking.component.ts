import { Component, Input, OnInit } from '@angular/core';
import { Player } from '../../shared/model/player';

@Component({
  selector: 'app-player-ranking',
  templateUrl: './player-ranking.component.html',
  styleUrls: ['./player-ranking.component.scss']
})
export class PlayerRankingComponent implements OnInit {
  @Input() players: Player[];
  displayedColumns = ['position', 'username', 'rating'];

  constructor() { }

  ngOnInit(): void { }
}
