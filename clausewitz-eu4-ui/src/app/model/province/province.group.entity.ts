export class ProvinceGroupEntity {
    id: number;
    name: string;
    owner: string;
    development: number = 0;

    static fromJsonObj(obj) {
        return Object.assign(new ProvinceGroupEntity(), obj);
    }
}
