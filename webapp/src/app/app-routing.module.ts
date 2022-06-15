import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LeagueListComponent } from './leagues/league-list/league-list.component';
import { LeagueComponent } from './leagues/league.component';
import { DashboardComponent } from './leagues/dashboard/dashboard.component';
import { PlayerListComponent } from './players/player-list/player-list.component';

const routes: Routes = [
  { path: '', redirectTo: '/leagues', pathMatch: 'full' },
  { path: 'leagues', component: LeagueListComponent },
  { path: 'leagues/:league_id', component: LeagueComponent, children: [
      { path: '', component: DashboardComponent },
      { path: 'players', component: PlayerListComponent }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
