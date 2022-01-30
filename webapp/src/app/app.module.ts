import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { GraphQLModule } from './graphql.module';
import { HttpClientModule } from '@angular/common/http';
import { HeaderComponent } from './shared/header/header.component';
import { MatToolbarModule } from '@angular/material/toolbar';
import { LeagueListComponent } from './leagues/league-list/league-list.component';
import { MatListModule } from '@angular/material/list';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSidenavModule } from '@angular/material/sidenav';
import { DashboardComponent } from './leagues/dashboard/dashboard.component';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { PlayerRankingComponent } from './players/player-ranking/player-ranking.component';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { RecentlyPlayedGames } from './games/recently-played-games/recently-played-games.component';
import { MatIconModule } from '@angular/material/icon';
import { ScheduledGamesComponent } from './games/scheduled-games/scheduled-games.component';

@NgModule({
    declarations: [
        AppComponent,
        HeaderComponent,
        LeagueListComponent,
        DashboardComponent,
        PlayerRankingComponent,
        RecentlyPlayedGames,
        ScheduledGamesComponent
    ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    GraphQLModule,
    HttpClientModule,
    MatToolbarModule,
    MatListModule,
    MatCardModule,
    MatButtonModule,
    MatProgressBarModule,
    MatSidenavModule,
    MatProgressSpinnerModule,
    MatTableModule,
    MatPaginatorModule,
    MatIconModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
