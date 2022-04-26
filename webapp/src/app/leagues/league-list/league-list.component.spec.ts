import { ComponentFixture, inject, TestBed } from '@angular/core/testing';

import { LeagueListComponent } from './league-list.component';
import { RouterTestingModule } from '@angular/router/testing';
import { MatCardModule } from '@angular/material/card';
import { MatListModule } from '@angular/material/list';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { LeagueService } from '../shared/league.service';
import { of } from 'rxjs';
import { Page } from '../../shared/model/page';
import { League } from '../../shared/model/league';
import { By } from '@angular/platform-browser';
import { MatButtonModule } from '@angular/material/button';
import { Router } from '@angular/router';
import { LEAGUES_PAGE } from '../../../testing/data';
import { MatIconModule } from '@angular/material/icon';
import { SnackbarService } from '../../shared/snackbar/snackbar.service';
import { SnackbarServiceStub } from '../../../testing/stubs';

describe('LeagueListComponent', () => {
  let component: LeagueListComponent;
  let fixture: ComponentFixture<LeagueListComponent>;
  let leagueServiceSpy = jasmine.createSpyObj('LeagueService', ['leagues', 'leaguesAfter', 'leaguesBefore']);

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ LeagueListComponent ],
      imports: [ RouterTestingModule, MatCardModule, MatListModule, MatProgressBarModule, MatButtonModule, MatIconModule ],
      providers: [
        { provide: LeagueService, useValue: leagueServiceSpy },
        { provide: SnackbarService, useClass: SnackbarServiceStub }
      ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    leagueServiceSpy.leagues.and.returnValue(of({data: { leagues: LEAGUES_PAGE }}));
    fixture = TestBed.createComponent(LeagueListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show leagues on the list', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    const listOptions = compiled.querySelectorAll('mat-list-option');
    expect(listOptions.length).toEqual(LEAGUES_PAGE.edges.length);
    expect(listOptions[0].textContent?.trim()).toEqual(LEAGUES_PAGE.edges[0].node.name);
    expect(listOptions[1].textContent?.trim()).toEqual(LEAGUES_PAGE.edges[1].node.name);
  });

  it('should load next leagues page after button click', () => {
    const nextLeaguesPage = {
      pageInfo: {
        hasPreviousPage: true, hasNextPage: false, startCursor: 'c4c4c4', endCursor: 'c4c4c4'
      },
      edges: [
        {
          cursor: 'c4c4c4',
          node: {
            id: 'ccc444', name: 'League-ccc444'
          }
        },
        {
          cursor: 'c5c5c5',
          node: {
            id: 'ccc555', name: 'League-ccc555'
          }
        }
      ]
    } as Page<League>;
    leagueServiceSpy.leaguesAfter.and.returnValue(of({data: { leagues: nextLeaguesPage }}));
    // click "Next page" button
    const pageButtons = fixture.debugElement.queryAll(By.css('button.mat-icon-button'));
    pageButtons[1].triggerEventHandler('click', null)
    fixture.detectChanges()
    // check leagues list and button state
    const listOptions = fixture.nativeElement.querySelectorAll('mat-list-option');
    expect(listOptions.length).toEqual(nextLeaguesPage.edges.length);
    expect(pageButtons[0].nativeElement.disabled).toBeFalse();
    expect(pageButtons[1].nativeElement.disabled).toBeTrue();
  });

  it('should load previous leagues page after button click', () => {
    const previousLeaguesPage = {
      pageInfo: {
        hasPreviousPage: false, hasNextPage: true, startCursor: 'c4c4c4', endCursor: 'c4c4c4'
      },
      edges: [
        {
          cursor: 'c4c4c4',
          node: {
            id: 'ccc444', name: 'League-ccc444'
          }
        },
        {
          cursor: 'c5c5c5',
          node: {
            id: 'ccc555', name: 'League-ccc555'
          }
        }
      ]
    } as Page<League>;
    leagueServiceSpy.leaguesBefore.and.returnValue(of({data: { leagues: previousLeaguesPage }}));
    // click "Previous page" button
    const pageButtons = fixture.debugElement.queryAll(By.css('button.mat-icon-button'));
    pageButtons[0].triggerEventHandler('click', null)
    fixture.detectChanges()
    // check leagues list and button state
    const listOptions = fixture.nativeElement.querySelectorAll('mat-list-option');
    expect(listOptions.length).toEqual(previousLeaguesPage.edges.length);
    expect(pageButtons[0].nativeElement.disabled).toBeTrue();
    expect(pageButtons[1].nativeElement.disabled).toBeFalse();
  });

  it('should redirect to chosen league', inject([Router], (router: Router) => {
    spyOn(router, 'navigate').and.stub();
    const listOptions = fixture.debugElement.queryAll(By.css('mat-list-option'));
    listOptions[1].triggerEventHandler('click', null)
    expect(router.navigate).toHaveBeenCalledOnceWith(['/leagues', LEAGUES_PAGE.edges[1].node.id])
  }));
});
