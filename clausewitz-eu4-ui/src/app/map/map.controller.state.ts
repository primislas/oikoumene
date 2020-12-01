import {ProvinceShape} from "../model/map/province.shape";
import {Border} from "../model/map/border";
import {NameCurve} from "../model/map/name.curve";
import {River} from "../model/map/river";
import {ProvinceListEntity} from "../model/province/province.list.entity";
import {TagMetadata} from "./tag.metadata";

export class MapControllerState {
  width: number = 5632;
  height: number = 2048;
  style: string = "";
  mode: string = "political";

  riverClasses = ["river-narrowest", "river-narrow", "river-wide", "river-widest"];

  provinceShapes: ProvinceShape[] = [];
  borders: Border[] = [];
  names: NameCurve[] = [];
  riversByClass: Map<string, River[]> = new Map<string, River[]>();

  provinces: Map<number, ProvinceListEntity> = new Map<number, ProvinceListEntity>();
  inhabitable: ProvinceShape[] = [];
  tags: Map<string, TagMetadata> = new Map<string, TagMetadata>();
}
