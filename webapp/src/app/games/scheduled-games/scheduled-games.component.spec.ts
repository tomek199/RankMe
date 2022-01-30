import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ScheduledGamesComponent } from './scheduled-games.component';

describe('ScheduledGamesComponent', () => {
  let component: ScheduledGamesComponent;
  let fixture: ComponentFixture<ScheduledGamesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ScheduledGamesComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ScheduledGamesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
