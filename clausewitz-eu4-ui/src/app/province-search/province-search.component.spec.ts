import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ProvinceSearchComponent } from './province-search.component';

describe('ProvinceSearchComponent', () => {
  let component: ProvinceSearchComponent;
  let fixture: ComponentFixture<ProvinceSearchComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ProvinceSearchComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProvinceSearchComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
