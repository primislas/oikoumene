import {Color} from "../model/map/color";
import {Mesh} from "three"
import {ProvinceListEntity} from "../model/province/province.list.entity";
import {Path} from "../model/map/Path";

export class ProvinceShape3d {
    provId: number;
    path: Path[];
    clip: Path[][];
    color: Color;
    mesh: Mesh;

    metadata: ProvinceListEntity;
}
