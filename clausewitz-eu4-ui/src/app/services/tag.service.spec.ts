import { TestBed } from '@angular/core/testing';

import { TagService } from './tag.service';

describe('TagServiceService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: TagService = TestBed.get(TagService);
    expect(service).toBeTruthy();
  });
});
