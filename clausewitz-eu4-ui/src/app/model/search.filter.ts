import {Entity} from './entity';

export class SearchFilter {
    id: string;
    name: string;
    value: any;
    values: Entity[] = [];
    options: Entity[] = [];

    constructor(id: string, name: string) {
        this.id = id;
        this.name = name;
    }

    addValue(v: Entity = new Entity()): SearchFilter {
        if (this.values.length > 0) return this;
        this.values.push(v);
        return this;
    }

    removeValue(index): SearchFilter {
        this.values.splice(index, 1);
        return this;
    }

    setOptions(options: Entity[]) {
        this.options = options;
    }

    fromValue(): SearchFilter {
        if (this.value && this.value !== "") this.values = [new Entity(this.value)];
        else this.values = [];

        return this;
    }

}
