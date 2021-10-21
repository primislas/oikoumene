import {Component, OnInit} from '@angular/core';
import {River} from "../model/map/river";
import {MapService} from "../services/map.service";
import {MapThreeDScene} from "./map-three-d.scene";
import {Border} from "../model/map/border";
import {TagMetadata} from "../map/tag.metadata";
import {ProvinceListEntity} from "../model/province/province.list.entity";
import {NameCurve} from "../model/map/name.curve";
import {Shape3d} from "./shape.3d";

@Component({
  selector: 'app-map-three-d',
  templateUrl: './map-three-d.component.html',
  styleUrls: ['./map-three-d.component.scss']
})
export class MapThreeDComponent implements OnInit {

  map: MapThreeDScene = new MapThreeDScene();

  constructor(
      private mapService: MapService
  ) {
  }

  ngOnInit() {
    this.map.init();
    this.loadMap();
  }

  loadMap() {
    this.mapService
        .loadMap()
        .subscribe(msg => this.onMapUpdate(msg, this.map));
  }

  onMapUpdate(update: { event, data }, map: MapThreeDScene) {
    const event = update.event;
    if (!event) {
      console.warn(`Received a malformed message with no event type specified: ${update}`)
      return;
    }

    const data = update.data;
    return this.onMapUpdateEvent(event, data, map);
  }

  // onMapMetadata(data, map: MapThreeDScene) {
  //   // const meta = <MapMetadata>data;
  //   return map;
  // }

  onMapRivers(data, map: MapThreeDScene) {
    const rivers = (data || []).map(r => <River>r);
    const headClass = (r: River): string => r.classes[0];
    map.riversByClass = rivers.reduce(function (rv: Map<string, River[]>, r: River) {
      const vs = rv.get(headClass(r)) || <River[]>[];
      vs.push(r);
      rv.set(headClass(r), vs);
      return rv;
    }, new Map<string, River[]>());

    return map;
  }

  onMapProvinceShape = (data, map: MapThreeDScene) => {
    const shapes = <Shape3d[]>(data || []);
    return map.addShapes(shapes)
  }

  onMapStyle(data, map: MapThreeDScene) {
    const style = <string>data;
    map.style = `${style}\n${map.style}`;
    return map;
  }

  onMapBorders(data, map: MapThreeDScene) {
    const borders = <Border[]>(data || []);
    return map.addBorders(borders);
  }

  onTagMetadata(data, map: MapThreeDScene) {
    const tags = <TagMetadata[]>(data || []);
    return map.addTags(tags);
  }

  onProvinceMetadata = (data, map: MapThreeDScene) => {
    const ps = <ProvinceListEntity[]>(data || []);
    return map.addProvinceMetadata(ps);
  }


  onMapNames(data, map: MapThreeDScene): MapThreeDScene {
    map.names = <NameCurve[]>(data || []);
    return map;
  }

  eventHandlers: Map<string, (data: any, map: MapThreeDScene) => MapThreeDScene> = new Map([
    ["mapStyle", this.onMapStyle],
    ["mapProvinceShapes", this.onMapProvinceShape],
    ["mapRivers", this.onMapRivers],
    ["mapBorders", this.onMapBorders],
    ["mapNames", this.onMapNames],
    // ["mapMetadata", this.onMapMetadata],
    ["tagMetadata", this.onTagMetadata],
    ["provinceMetadata", this.onProvinceMetadata],
  ]);
  ignoredEvents = new Set<string>(["loadMap", "mapMetadata"]);

  onMapUpdateEvent(event: string, data: any, map: MapThreeDScene): MapThreeDScene {
    if (this.ignoredEvents.has(event))
      return map;

    const f = this.eventHandlers.get(event);
    if (!f) {
      console.warn(`Received an unrecognized map update event: ${event}`);
      return map;
    }
    else
      return f(data, map);
  }

}
