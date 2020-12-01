import {Component, OnInit} from '@angular/core';
import {River} from "../model/map/river";
import {MapService} from "../services/map.service";
import {MapControllerState} from "./map.controller.state";
import {Border} from "../model/map/border";
import {ProvinceShape} from "../model/map/province.shape";
import {TagMetadata} from "./tag.metadata";
import {ProvinceListEntity} from "../model/province/province.list.entity";
import {NameCurve} from "../model/map/name.curve";

@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.scss']
})
export class MapComponent implements OnInit {

  map: MapControllerState = new MapControllerState();

  constructor(
    private mapService: MapService
  ) {
  }

  ngOnInit() {
    this.loadMap();
  }

  loadMap() {
    this.mapService
      .loadMap()
      .subscribe(msg => this.onMapUpdate(msg, this.map));
  }

  onMapUpdate(update: { event, data }, map: MapControllerState) {
    const event = update.event;
    if (!event) {
      console.warn(`Received a malformed message with no event type specified: ${update}`)
      return;
    }

    const data = update.data;
    return this.onMapUpdateEvent(event, data, map);
  }

  onMapRivers(data, map: MapControllerState) {
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

  onMapProvinceShape = (data, map: MapControllerState) => {
    map.provinceShapes = <ProvinceShape[]>(data || []);
    if (map.provinces.size > 0)
      this.mergeProvinceMetadata(map);
    return map;
  }

  onMapStyle(data, map: MapControllerState) {
    const style = <string>data;
    map.style = `${style}\n${map.style}`;
    return map;
  }

  onMapBorders(data, map: MapControllerState) {
    map.borders = (data || [])
      .map(bGroup => (bGroup.paths || [])
        .map(path => {
          const b = new Border();
          b.path = path;
          b.classes = bGroup.classes;
          return b;
        })
      )
      .reduce((acc, a) => acc.concat(a), []);
    return map;
  }

  onTagMetadata(data, map: MapControllerState) {
    const tags = <TagMetadata[]>(data || []);
    map.tags = tags
      .reduce(
        (acc, t) => {
          acc.set(t.id, t);
          return acc;
        },
        new Map<string, TagMetadata>()
      );
    const tagStyles = tags.map(t => `.${t.id} { fill:rgb(${t.color.r},${t.color.g},${t.color.b}) }`);
    map.style = [map.style].concat(tagStyles).join("\n");

    return map;
  }

  onProvinceMetadata = (data, map: MapControllerState) => {
    const ps = <ProvinceListEntity[]>(data || []);
    map.provinces = ps
      .reduce(
        (acc, p) => {
          acc.set(p.id, p);
          return acc;
        },
        new Map<number, ProvinceListEntity>()
      );
    if (map.provinceShapes.length > 0)
      this.mergeProvinceMetadata(map);
    return map;
  }

  mergeProvinceMetadata(map: MapControllerState) {
    map.provinceShapes.forEach(shape => this.addShapeMetadata(shape, map.provinces));
    return map;
  }

  addShapeMetadata(
    shape: ProvinceShape,
    provinces: Map<number, ProvinceListEntity> = new Map<number, ProvinceListEntity>()
  ) {
    const pm = provinces.get(shape.provinceId);
    if (pm) {
      if (pm.owner) shape.classes.push(pm.owner);
      shape.name = `${pm.name} (#${pm.id})`;
    }
  }

  onMapNames(data, map: MapControllerState): MapControllerState {
    map.names = <NameCurve[]>(data || []);
    return map;
  }

  eventHandlers: Map<string, (data: any, map: MapControllerState) => MapControllerState> = new Map([
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

  onMapUpdateEvent(event: string, data: any, map: MapControllerState): MapControllerState {
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
