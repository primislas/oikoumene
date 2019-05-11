import { TestBed } from '@angular/core/testing';

import { ProvinceService } from './province.service';

describe('ProvinceService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: ProvinceService = TestBed.get(ProvinceService);
    expect(service).toBeTruthy();
  });
});
