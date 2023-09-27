import { TestBed } from '@angular/core/testing';

import { FirstVisitGuard } from './first-visit.guard';

describe('FirstVisitGuard', () => {
  let guard: FirstVisitGuard;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    guard = TestBed.inject(FirstVisitGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });
});
