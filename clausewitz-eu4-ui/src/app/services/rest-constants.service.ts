import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class RestConstantsService {
  private readonly address = "localhost:4200"

  readonly eu4Service = "/clausewitz/eu4";
  readonly provinceSearchEndpoint = `${this.eu4Service}/provinces`;
  readonly tagSearchEndpoint = `${this.eu4Service}/tags`;

  readonly mapWsEndpoint = `ws://${this.address}${this.eu4Service}/map/data`

  constructor() { }
}
