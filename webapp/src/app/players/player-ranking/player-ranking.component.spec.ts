import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PlayerRankingComponent } from './player-ranking.component';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { LEAGUE_WITH_PLAYERS_AND_GAMES } from '../../../testing/data';
import { MatIconModule } from '@angular/material/icon';

describe('PlayerRankingComponent', () => {
  let component: PlayerRankingComponent;
  let fixture: ComponentFixture<PlayerRankingComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PlayerRankingComponent ],
      imports: [ MatCardModule, MatTableModule, MatIconModule ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PlayerRankingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show "No data" row when there is no players', () => {
    component.players = [];
    fixture.detectChanges();
    const noDataRow = fixture.nativeElement.querySelector('table tbody tr td');
    expect(noDataRow.textContent).toEqual("No data");
  });

  it('should show players in table', () => {
    component.players = LEAGUE_WITH_PLAYERS_AND_GAMES.players;
    fixture.detectChanges();
    const rows = fixture.nativeElement.querySelectorAll('table tbody tr');
    rows.forEach((row: Element, index: number) => {
      const columns = row.querySelectorAll('td');
      expect(columns[0].textContent).toEqual(String(index + 1));
      expect(columns[1].textContent).toEqual(LEAGUE_WITH_PLAYERS_AND_GAMES.players[index].name)
      expect(columns[2].textContent).toEqual(String(LEAGUE_WITH_PLAYERS_AND_GAMES.players[index].rating));
    })
  });
});
