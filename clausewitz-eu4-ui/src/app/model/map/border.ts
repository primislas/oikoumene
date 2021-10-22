import {Mesh} from "three";
import {Path} from "./Path";
import {Point} from "./point";

export class Border {
    id: number;
    path: string;
    paths: Path[];
    type: string;
    mesh: Mesh;
    lProv: number = 0;
    rProv: number = 0;
    classes: string[] = [];

    asPoints(bezierScale: number = 1): Point[] {
        return Border.asPoints(this, bezierScale);
    }

    static asPoints(b: Border, bezierScale: number = 1): Point[] {
        return (b.paths || [])
            .map(path => Path.asPoints(path, bezierScale))
            .reduce((acc, a) => acc.concat(a), []);
    }

}
