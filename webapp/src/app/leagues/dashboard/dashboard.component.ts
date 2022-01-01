import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { League } from '../../shared/model/league';
import { LeagueService } from '../shared/league.service';

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
    private leagueService: LeagueService
  ) { }

  ngOnInit(): void {
    this.getLeague();
  }

  getLeague() {
    this.route.params.subscribe(params => {
      this.isLoading = true;
      const leagueId = params['league_id'];
      this.leagueService.leagueWithPlayersAndGames(leagueId).subscribe(({data}) => {
        this.isLoading = false;
        this.league = data.league;
      })
    });
  }
}
