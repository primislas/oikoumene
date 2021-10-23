export class ProvinceListEntity {
    id: number;
    name: string;
    owner: string;
    cores: string[] = [];
    is_city: boolean;

    religion: string;
    culture: string;

    development: number = 0;
    tradeGood: string;
    tradeNode: string;

    climate: string[] = [];
    type: string;

    static fromJsonObj(obj) {
        return Object.assign(new ProvinceListEntity(), obj);
    }
}
