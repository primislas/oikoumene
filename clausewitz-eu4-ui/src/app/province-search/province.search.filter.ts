import {Entity} from '../model/entity';

export class ProvinceSearchFilter {
    id: string;
    name: string;
    values: Entity[] = [];
    // TODO: display select if options are available
    options: Entity[] = [];
    select: () => Entity;

    constructor(id: string, name: string) {
        this.id = id;
        this.name = name;
    }

    addValue(v: Entity = new Entity()): ProvinceSearchFilter {
        if (this.values.length > 0) return;
        // const e = this.select();
        // if (e) this.values.push(e);
        this.values.push(v);
        return this;
    }

    removeValue(index): ProvinceSearchFilter {
        this.values.splice(index, 1);
        return this;
    }

    setOptions(options: Entity[]) {
        this.options = options;
    }

}
