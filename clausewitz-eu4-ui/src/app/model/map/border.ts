import {Mesh} from "three";
import {Path} from "./Path";

export class Border {
    id: number;
    path: string;
    paths: Path[];
    type: string;
    mesh: Mesh;
    lProv: number = 0;
    rProv: number = 0;
    classes: string[] = [];
}
