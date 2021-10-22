import {Point} from "./point";
import * as adaptiveBezierPoints from 'adaptive-bezier-curve';

export class Path {
    polyline: Point[];
    bezier: Point[];

    asPoints(bezierScale: number = 1): Point[] {
        return Path.asPoints(this, bezierScale);
    }

    static asPoints(p: Path, bezierScale: number = 1): Point[] {
        if (p.polyline)
            return p.polyline
        else if (p.bezier) {
            const [s, cp1, cp2, e] = p.bezier;
            return adaptiveBezierPoints(s, cp1, cp2, e, bezierScale);
        } else
            return [];
    }
}
