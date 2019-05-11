import {ProvinceGroupEntity} from './province.group.entity';

export class ProvinceGroup {
    value: any;
    development: number = 0;
    entities: ProvinceGroupEntity[] = [];

    static fromJsonObj(obj) {
        return Object.assign(new ProvinceGroup(), obj);
    }
}
