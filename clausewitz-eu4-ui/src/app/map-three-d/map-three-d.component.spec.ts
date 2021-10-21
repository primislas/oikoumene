import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MapThreeDComponent } from './map-three-d.component';

describe('MapThreeDComponent', () => {
  let component: MapThreeDComponent;
  let fixture: ComponentFixture<MapThreeDComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MapThreeDComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MapThreeDComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
