import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HeaderComponent } from './header.component';
import { MatToolbarModule } from '@angular/material/toolbar';
import { InfoService } from './info.service';
import { of } from 'rxjs';
import { ApolloQueryResult } from '@apollo/client';

describe('HeaderComponent', () => {
  let component: HeaderComponent;
  let fixture: ComponentFixture<HeaderComponent>;
  let infoServiceSpy = jasmine.createSpyObj('InfoService', ['apiVersion'])

  beforeEach(async () => {
    infoServiceSpy.apiVersion.and.returnValue(of({data: {info: 'API 1.2.3'}} as ApolloQueryResult<any>));
    await TestBed.configureTestingModule({
      declarations: [ HeaderComponent ],
      imports: [ MatToolbarModule ],
      providers: [
        { provide: InfoService, useValue: infoServiceSpy }
      ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(HeaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show API version in toolbar', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('span.api-version')?.textContent).toBe('API 1.2.3');
  });
});
