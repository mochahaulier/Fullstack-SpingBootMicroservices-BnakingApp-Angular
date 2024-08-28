import { TestBed } from '@angular/core/testing';

import { ClientproductService } from './clientproduct.service';

describe('ClientproductService', () => {
  let service: ClientproductService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ClientproductService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
