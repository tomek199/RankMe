import { Component, OnInit } from '@angular/core';
import { LeagueService } from '../shared/league.service';
import { League } from '../../shared/model/league';
import { Page, PageInfo } from '../../shared/model/page';
import { Router } from '@angular/router';
import { SnackbarService } from '../../shared/snackbar/snackbar.service';
import { ApolloQueryResult } from '@apollo/client';
import { MatDialog } from '@angular/material/dialog';
import { CreateLeagueComponent } from '../create-league/create-league.component';

@Component({
  selector: 'app-league-list',
  templateUrl: './league-list.component.html',
  styleUrls: ['./league-list.component.scss']
})
export class LeagueListComponent implements OnInit {
  private PAGE_SIZE = 5;

  isLoading: boolean = false;
  pageInfo: PageInfo | null;
  leagues: League[];

  constructor(
    private leagueService: LeagueService,
    private router: Router,
    private snackbarService: SnackbarService,
    private dialog: MatDialog,
  ) { }

  ngOnInit(): void {
    this.list();
  }

  private list() {
    this.isLoading = true;
    this.leagueService.leagues(this.PAGE_SIZE).subscribe({
      next: this.updatePage,
      error: this.snackbarService.handleError,
      complete: () => this.isLoading = false
    });
  }

  loadPreviousPage() {
    this.isLoading = true;
    this.leagueService.leaguesBefore(this.PAGE_SIZE, this.pageInfo?.startCursor!).subscribe({
      next: this.updatePage,
      error: this.snackbarService.handleError,
      complete: () => this.isLoading = false
    });
  }

  loadNextPage() {
    this.isLoading = true;
    this.leagueService.leaguesAfter(this.PAGE_SIZE, this.pageInfo?.endCursor!).subscribe({
      next: this.updatePage,
      error: this.snackbarService.handleError,
      complete: () => this.isLoading = false
    });
  }

  private updatePage = (result: ApolloQueryResult<{ leagues: Page<League> }>) => {
    this.pageInfo = result.data.leagues.pageInfo;
    this.leagues = result.data.leagues.edges.map(edge => edge.node);
  }

  select(id: string) {
    this.router.navigate(['/leagues', id])
  }

  openCreateLeagueDialog() {
    this.dialog.open(CreateLeagueComponent);
  }
}
