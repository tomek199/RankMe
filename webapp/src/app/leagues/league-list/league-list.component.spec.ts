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

describe('LeagueListComponent', () => {
  let component: LeagueListComponent;
  let fixture: ComponentFixture<LeagueListComponent>;
  let leagueServiceSpy = jasmine.createSpyObj('LeagueService', ['leagues']);

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ LeagueListComponent ],
      imports: [ RouterTestingModule, MatCardModule, MatListModule, MatProgressBarModule, MatButtonModule ],
      providers: [
        { provide: LeagueService, useValue: leagueServiceSpy }
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
    expect(listOptions.length).toEqual(2);
    expect(listOptions[0].textContent?.trim()).toEqual(LEAGUES_PAGE.edges[0].node.name);
    expect(listOptions[1].textContent?.trim()).toEqual(LEAGUES_PAGE.edges[1].node.name);
  });

  it('should load more leagues after button click', () => {
    const moreLeaguesPage = {
      pageInfo: {
        hasPreviousPage: true, hasNextPage: false, startCursor: 'c3c3c3', endCursor: 'c3c3c3'
      },
      edges: [
        {
          cursor: 'c3c3c3',
          node: {
            id: 'ccc333', name: 'League-ccc333'
          }
        }
      ]
    } as Page<League>;
    leagueServiceSpy.leagues.and.returnValue(of({data: { leagues: moreLeaguesPage }}));
    // click "Load more" button
    const loadMoreButton = fixture.debugElement.query(By.css('button.mat-button'));
    loadMoreButton.triggerEventHandler('click', null)
    fixture.detectChanges()
    // check leagues list and button state
    const listOptions = fixture.nativeElement.querySelectorAll('mat-list-option');
    expect(listOptions.length).toEqual(3);
    expect(listOptions[2].textContent?.trim()).toEqual(moreLeaguesPage.edges[0].node.name);
    expect(loadMoreButton.nativeElement.disabled).toBeTrue()
  });

  it('should redirect to chosen league', inject([Router], (router: Router) => {
    spyOn(router, 'navigate').and.stub();
    const listOptions = fixture.debugElement.queryAll(By.css('mat-list-option'));
    listOptions[1].triggerEventHandler('click', null)
    expect(router.navigate).toHaveBeenCalledOnceWith(['/leagues', LEAGUES_PAGE.edges[1].node.id])
  }));
});
