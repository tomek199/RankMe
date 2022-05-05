import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateLeagueComponent } from './create-league.component';

describe('CreateLeagueComponent', () => {
  let component: CreateLeagueComponent;
  let fixture: ComponentFixture<CreateLeagueComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CreateLeagueComponent ]
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
});
