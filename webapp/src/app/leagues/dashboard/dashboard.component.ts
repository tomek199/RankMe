import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { League } from '../../shared/model/league';
import { LeagueService } from '../shared/league.service';
import { ErrorHandlerService } from '../../shared/error-handler/error-handler.service';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  isLoading: boolean = false;
  league: League;

  constructor(
    private route: ActivatedRoute,
    private leagueService: LeagueService,
    private errorHandler: ErrorHandlerService
  ) { }

  ngOnInit(): void {
    this.getLeague();
  }

  getLeague() {
    this.route.params.subscribe(params => {
      this.isLoading = true;
      const leagueId = params['league_id'];
      this.leagueService.leagueWithPlayersAndGames(leagueId)
        .subscribe(({data}) => this.league = data.league,
          this.errorHandler.handle).add(() => this.isLoading = false);
    });
  }
}
