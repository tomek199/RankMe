import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { CreateLeagueComponent } from './create-league/create-league.component';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-league',
  templateUrl: './league.component.html',
  styleUrls: ['./league.component.scss']
})
export class LeagueComponent implements OnInit {
  leagueId: string;

  constructor(
    private route: ActivatedRoute,
    private dialog: MatDialog,
  ) { }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.leagueId = params['league_id'];
    });
  }

  createLeague() {
    this.dialog.open(CreateLeagueComponent);
  }
}
