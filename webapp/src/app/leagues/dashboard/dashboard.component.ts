import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { League } from '../../shared/model/league';
import { LeagueService } from '../shared/league.service';
import { SnackbarService } from '../../shared/snackbar/snackbar.service';

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
    private router: Router,
    private leagueService: LeagueService,
    private snackbarService: SnackbarService
  ) { }

  ngOnInit(): void {
    this.getLeague();
  }

  getLeague() {
    this.route.params.subscribe(params => {
      this.isLoading = true;
      const leagueId = params['league_id'];
      this.leagueService.leagueWithPlayersAndGames(leagueId)
        .subscribe({
          next: ({data}) => {
            if (data.league) this.league = data.league;
            else this.router.navigate(['/leagues']);
          },
          error: this.snackbarService.handleError,
          complete: () => this.isLoading = false
        });
    });
  }
}
