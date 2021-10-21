import {Curve} from "./curve";
import {Color} from "../model/map/color";
import {Mesh} from "three"
import {ProvinceListEntity} from "../model/province/province.list.entity";

export class Shape3d {
    provId: number;
    path: Curve[];
    color: Color;
    mesh: Mesh;

    metadata: ProvinceListEntity;
}
