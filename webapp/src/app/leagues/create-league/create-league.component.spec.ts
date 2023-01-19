import { ComponentFixture, inject, TestBed } from '@angular/core/testing';

import { CreateLeagueComponent } from './create-league.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { SnackbarService } from '../../shared/snackbar/snackbar.service';
import { LeagueService } from '../shared/league.service';
import { LEAGUE, SUBMITTED } from '../../../testing/data';
import { of } from 'rxjs';
import { By } from '@angular/platform-browser';
import { CreateLeagueCommand } from '../shared/league.model';
import { RouterTestingModule } from '@angular/router/testing';
import { Router } from '@angular/router';

describe('CreateLeagueComponent', () => {
  let component: CreateLeagueComponent;
  let fixture: ComponentFixture<CreateLeagueComponent>;
  let leagueServiceSpy = jasmine.createSpyObj('LeagueService', ['createLeague', 'leagueCreated']);
  let snackbarServiceSpy = jasmine.createSpyObj('SnackbarService', ['showMessage']);
  let dialogRefSpy = jasmine.createSpyObj('MatDialogRef', ['close']);

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CreateLeagueComponent ],
      imports: [
        RouterTestingModule, FormsModule, ReactiveFormsModule, BrowserAnimationsModule,
        MatDialogModule, MatFormFieldModule, MatInputModule, MatIconModule
      ],
      providers: [
        { provide: MatDialogRef, useValue: dialogRefSpy },
        { provide: SnackbarService, useValue: snackbarServiceSpy },
        { provide: LeagueService, useValue: leagueServiceSpy }
      ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CreateLeagueComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should mark form invalid when league name is empty', () => {
    expect(component.createLeagueForm.invalid).toBeTrue();
  });

  it('should create league', inject([Router], (router: Router) => {
    spyOn(router, 'navigate').and.stub();
    leagueServiceSpy.createLeague.and.returnValue(of({data: {createLeague: SUBMITTED}}));
    leagueServiceSpy.leagueCreated.and.returnValue(of({data: {leagueCreated: LEAGUE}}));
    component.createLeagueForm.controls.name.setValue(LEAGUE.name);
    expect(component.createLeagueForm.invalid).toBeFalse();
    const submitButton = fixture.debugElement.query(By.css('form'));
    submitButton.triggerEventHandler('submit', null);
    expect(leagueServiceSpy.createLeague).toHaveBeenCalledOnceWith(new CreateLeagueCommand(LEAGUE.name));
    expect(dialogRefSpy.close).toHaveBeenCalledTimes(1);
    expect(snackbarServiceSpy.showMessage).toHaveBeenCalledOnceWith(`League ${LEAGUE.name} created!`);
    expect(router.navigate).toHaveBeenCalledOnceWith(['/leagues', LEAGUE.id]);
  }));
});
