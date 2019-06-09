import {Entity} from '../model/entity';

export class TagSearchFilter {
    id: string;
    name: string;
    value: string;
    values: Entity[] = [];
    options: Entity[] = [];

    constructor(id: string, name: string) {
        this.id = id;
        this.name = name;
    }

    addValue(v: Entity = new Entity()): TagSearchFilter {
        if (this.values.length > 0) return;
        // const e = this.select();
        // if (e) this.values.push(e);
        this.values.push(v);
        return this;
    }

    removeValue(index): TagSearchFilter {
        this.values.splice(index, 1);
        return this;
    }


}
