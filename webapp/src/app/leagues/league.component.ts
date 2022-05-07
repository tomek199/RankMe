import { Component } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { CreateLeagueComponent } from './create-league/create-league.component';

@Component({
  selector: 'app-league',
  templateUrl: './league.component.html',
  styleUrls: ['./league.component.scss']
})
export class LeagueComponent {

  constructor(private dialog: MatDialog) { }

  createLeague() {
    this.dialog.open(CreateLeagueComponent);
  }
}
