import { TestBed } from '@angular/core/testing';

import { RestConstantsService } from './rest-constants.service';

describe('RestConstantsService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: RestConstantsService = TestBed.get(RestConstantsService);
    expect(service).toBeTruthy();
  });
});
