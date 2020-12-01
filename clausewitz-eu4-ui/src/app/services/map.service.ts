import {Injectable} from '@angular/core';
import {webSocket} from 'rxjs/webSocket';
import {RestConstantsService} from "./rest-constants.service";

@Injectable({
  providedIn: 'root'
})
export class MapService {

  private readonly subject;

  constructor(
    private restConstants: RestConstantsService,
  ) {
    this.subject = webSocket(restConstants.mapWsEndpoint);
  }

  loadMap() {
    this.subject
      .subscribe(
        msg => console.log(`Async event: ${msg.event}`),
        err => console.error(err),
        () => console.log("Unsubscribed from map websocket")
      );
    this.subject.next({event: "loadMap"});
    return this.subject;
  }

}
