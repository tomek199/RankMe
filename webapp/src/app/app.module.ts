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
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { PlayGameComponent } from './games/play-game/play-game.component';
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatOptionModule } from '@angular/material/core';
import { MatInputModule } from '@angular/material/input';
import { MatSliderModule } from '@angular/material/slider';
import { MatTooltipModule } from '@angular/material/tooltip';
import { CreateLeagueComponent } from './leagues/create-league/create-league.component';
import { LeagueComponent } from './leagues/league.component';
import { PlayerListComponent } from './players/player-list/player-list.component';
import { AddPlayerComponent } from './players/add-player/add-player.component';

@NgModule({
    declarations: [
        AppComponent,
        HeaderComponent,
        LeagueListComponent,
        DashboardComponent,
        PlayerRankingComponent,
        RecentlyPlayedGames,
        ScheduledGamesComponent,
        PlayGameComponent,
        CreateLeagueComponent,
        LeagueComponent,
        PlayerListComponent,
        AddPlayerComponent
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
        MatIconModule,
        MatSnackBarModule,
        MatDialogModule,
        MatFormFieldModule,
        MatInputModule,
        ReactiveFormsModule,
        MatAutocompleteModule,
        MatOptionModule,
        MatSliderModule,
        FormsModule,
        MatTooltipModule
    ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
