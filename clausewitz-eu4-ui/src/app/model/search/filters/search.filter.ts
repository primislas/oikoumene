import {Entity} from '../../entity';
import {SearchFilterDisplayType} from './search.filter.display.type';
import {SearchFilterType} from './search.filter.type';
import {Observable, of} from 'rxjs';
import {SearchedValue} from './searched.value';

export abstract class SearchFilter<T> {
    id: string;
    name: string;

    type: SearchFilterType;
    display: SearchFilterDisplayType;

    isMultiValue: Boolean = false;
    value: T;
    values: T[] = [];
    options: T[] = [];

    img: (T) => String;
    searchModal: () => Observable<Entity[]> = () => of([]);

    static toQueryParams(filters: SearchFilter<any>[] = []): string {
        return filters
            .map(f => f.queryParams())
            .map(o => Object.entries(o))
            .reduce((acc, a) => acc.concat(a), [])
            .map(([k,vs]) => vs.map(v => `${k}=${v}`))
            .reduce((acc, a) => acc.concat(a), [])
            .join("&");
    }

    protected constructor(
        id: string,
        name: string,
        type: SearchFilterType = SearchFilterType.Field,
        display: SearchFilterDisplayType = SearchFilterDisplayType.TextInput,
        isMultiValue: Boolean = false,
        options: T[] = [],
        img: (T) => String = undefined,
        searchModal: () => Observable<Entity[]> = () => of([]),
    ) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.display = display;
        this.isMultiValue = isMultiValue;
        this.options = options;
        this.img = img;
        this.searchModal = searchModal;
    }

    isField(): Boolean {
        return this.type === SearchFilterType.Field;
    }

    isEntity(): Boolean {
        return this.type === SearchFilterType.Entity;
    }

    abstract addValue(): SearchFilter<T>;

    removeValue(index): SearchFilter<T> {
        this.values.splice(index, 1);
        return this;
    }

    setOptions(options: T[]): SearchFilter<T> {
        this.options = options;
        return this;
    }

    queryParams(): object {
        const r = {};

        if (!this.isMultiValue) {
            if (this.values.length > 1) {
                this.values = [this.values[0]];
            } else if (this.value !== undefined && (typeof this.value !== 'string' || this.value !== '')) {
                this.values = [this.value];
            }
        }

        if (this.values.length > 0) {
            r[this.id] = this.values
                .map(v => {
                    if (v instanceof Entity) {
                        return v.id;
                    } else if (v instanceof SearchedValue) {
                        return v.value;
                    } else if (typeof v === "string") {
                        return v;
                    } else {
                        return v.toString();
                    }
                });
        }

        return r;
    }

}
