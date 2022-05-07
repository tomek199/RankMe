import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LeagueComponent } from './league.component';
import { RouterTestingModule } from '@angular/router/testing';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatDialog } from '@angular/material/dialog';
import { By } from '@angular/platform-browser';

describe('LeagueComponent', () => {
  let component: LeagueComponent;
  let fixture: ComponentFixture<LeagueComponent>;
  let matDialogSpy = jasmine.createSpyObj('MatDialog', ['open']);

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule, BrowserAnimationsModule, MatSidenavModule, MatListModule, MatIconModule
      ],
      declarations: [ LeagueComponent ],
      providers: [
        { provide: MatDialog, useValue: matDialogSpy }
      ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LeagueComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should open "Create league" dialog', () => {
    const createLeagueButton = fixture.debugElement.queryAll(By.css('mat-nav-list a'))[0];
    expect(createLeagueButton.nativeElement.textContent).toContain('Create league');
    createLeagueButton.triggerEventHandler('click', null);
    expect(matDialogSpy.open).toHaveBeenCalledTimes(1);
  });
});
