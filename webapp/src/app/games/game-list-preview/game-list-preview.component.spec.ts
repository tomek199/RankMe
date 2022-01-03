import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GameListPreviewComponent } from './game-list-preview.component';

describe('GameListPreviewComponent', () => {
  let component: GameListPreviewComponent;
  let fixture: ComponentFixture<GameListPreviewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ GameListPreviewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(GameListPreviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
