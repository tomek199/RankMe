import { Component, OnInit } from '@angular/core';
import { LeagueService } from '../shared/league.service';
import { League } from '../../shared/model/league';
import { PageInfo } from '../../shared/model/page';
import { Router } from '@angular/router';
import { ErrorHandlerService } from '../../shared/error-handler/error-handler.service';

@Component({
  selector: 'app-league-list',
  templateUrl: './league-list.component.html',
  styleUrls: ['./league-list.component.scss']
})
export class LeagueListComponent implements OnInit {
  private PAGE_SIZE = 3;

  isLoading: boolean = false;
  pageInfo: PageInfo;
  leagues: League[];

  constructor(
    private leagueService: LeagueService,
    private router: Router,
    private errorHandler: ErrorHandlerService
  ) { }

  ngOnInit(): void {
    this.list();
  }

  private list() {
    this.isLoading = true;
    this.leagueService.leagues(this.PAGE_SIZE).subscribe(({data}) => {
      this.pageInfo = data.leagues.pageInfo;
      this.leagues = data.leagues.edges.map(edge => edge.node);
    }, this.errorHandler.handle).add(() => this.isLoading = false);
  }

  loadMore() {
    this.isLoading = true;
    this.leagueService.leagues(this.PAGE_SIZE, this.pageInfo.endCursor!).subscribe(({data}) => {
      this.pageInfo = data.leagues.pageInfo;
      this.leagues.push(...data.leagues.edges.map(edge => edge.node));
      this.isLoading = false;
    });
  }

  select(id: string) {
    this.router.navigate(['/leagues', id])
  }
}
