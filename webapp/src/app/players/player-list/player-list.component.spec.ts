import { ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';

import { PlayerListComponent } from './player-list.component';
import { ActivatedRouteStub, SnackbarServiceStub } from '../../../testing/stubs';
import { ActivatedRoute } from '@angular/router';
import { SnackbarService } from '../../shared/snackbar/snackbar.service';
import { PlayerService } from '../shared/player.service';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { LEAGUE_WITH_PLAYERS, LEAGUE_WITH_PLAYERS_AND_GAMES, PLAYERS } from '../../../testing/data';
import { of } from 'rxjs';
import { By } from '@angular/platform-browser';
import { MatDialog } from '@angular/material/dialog';
import { AddPlayerComponent } from '../add-player/add-player.component';

describe('PlayerListComponent', () => {
  let component: PlayerListComponent;
  let fixture: ComponentFixture<PlayerListComponent>;
  let activatedRouteStub: ActivatedRouteStub;
  let playerServiceSpy = jasmine.createSpyObj('PlayerService', ['players', 'playerCreated']);
  let matDialogSpy = jasmine.createSpyObj('MatDialog', ['open']);
  let snackbarServiceSpy = jasmine.createSpyObj('SnackbarService', ['showMessage']);

  beforeEach(async () => {
    activatedRouteStub = new ActivatedRouteStub();
    await TestBed.configureTestingModule({
      declarations: [ PlayerListComponent ],
      imports: [ MatCardModule, MatProgressSpinnerModule, MatIconModule, MatTableModule ],
      providers: [
        { provide: ActivatedRoute, useValue: activatedRouteStub },
        { provide: PlayerService, useValue: playerServiceSpy },
        { provide: SnackbarService, useValue: snackbarServiceSpy },
        { provide: MatDialog, useValue: matDialogSpy }
      ]
    })
    .compileComponents();
    playerServiceSpy.playerCreated.and.returnValue({ subscribe: () => {} });
  });

  beforeEach(() => {
    activatedRouteStub.testParams = {league_id: LEAGUE_WITH_PLAYERS.id};
    fixture = TestBed.createComponent(PlayerListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show "No data" row when there is no league id specified',() => {
    activatedRouteStub.disableParent();
    component.ngOnInit();
    fixture.detectChanges();
    const noDataRow = fixture.nativeElement.querySelector('mat-card mat-card-content table tbody tr td');
    expect(noDataRow.textContent).toEqual("No data");
  });

  it('should show "No data" row when there is no players returned',() => {
    playerServiceSpy.players.and.returnValue(of({data: {players: []}}));
    component.ngOnInit();
    fixture.detectChanges();
    const noDataRow = fixture.nativeElement.querySelector('mat-card mat-card-content table tbody tr td');
    expect(noDataRow.textContent).toEqual("No data");
  });

  it('should show players in table',() => {
    playerServiceSpy.players.and.returnValue(of({data: {players: LEAGUE_WITH_PLAYERS.players}}));
    component.ngOnInit();
    fixture.detectChanges();
    const rows = fixture.nativeElement.querySelectorAll('table tbody tr');
    rows.forEach((row: Element, index: number) => {
      const columns = row.querySelectorAll('td');
      expect(columns[0].textContent).toEqual(LEAGUE_WITH_PLAYERS.players[index].name);
      expect(columns[1].textContent).toEqual(String(LEAGUE_WITH_PLAYERS_AND_GAMES.players[index].deviation))
      expect(columns[2].textContent).toEqual(String(LEAGUE_WITH_PLAYERS_AND_GAMES.players[index].rating));
    })
  });

  it('should open "Add player" dialog', () => {
    playerServiceSpy.players.and.returnValue(of({data: {players: []}}));
    component.ngOnInit();
    fixture.detectChanges();
    const addPlayerButton = fixture.debugElement.query(By.css('mat-card-actions button'));
    expect(addPlayerButton.nativeElement.textContent).toContain('Add player');
    addPlayerButton.triggerEventHandler('click', null);
    expect(matDialogSpy.open).toHaveBeenCalledWith(AddPlayerComponent, {data: LEAGUE_WITH_PLAYERS.id})
  });

  it('should update table by new player when created', () => {
    playerServiceSpy.players.and.returnValue(of({data: {players: LEAGUE_WITH_PLAYERS.players}}));
    playerServiceSpy.playerCreated.and.returnValue(of({data: {playerCreated: PLAYERS[0]}}));
    component.ngOnInit();
    fixture.detectChanges();
    const rows = fixture.nativeElement.querySelectorAll('table tbody tr');
    expect(rows.length).toEqual(LEAGUE_WITH_PLAYERS.players.length + 1);
  })
});
