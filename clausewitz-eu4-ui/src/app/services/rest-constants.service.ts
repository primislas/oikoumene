import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class RestConstantsService {
  readonly eu4Service = "/clausewitz/eu4";
  readonly provinceSearchEndpoint = `${this.eu4Service}/provinces`;

  constructor() { }
}
