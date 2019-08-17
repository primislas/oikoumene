import {SearchFilter} from './search.filter';
import {Entity} from '../../entity';
import {SearchFilterType} from './search.filter.type';
import {SearchFilterDisplayType} from './search.filter.display.type';
import {Observable} from 'rxjs';

export class EntitySearchFilter extends SearchFilter<Entity> {

    constructor(
        id: string,
        name: string,
        display: SearchFilterDisplayType = SearchFilterDisplayType.Modal,
        isMultiValue: Boolean = false,
        options: Entity[] = undefined,
        img: (T) => String = undefined,
        searchModal: () => Observable<Entity[]> = undefined,
    ) {
        super(id, name, SearchFilterType.Entity, display, isMultiValue, options, img, searchModal);
    }

    addValue(v: Entity = new Entity()): SearchFilter<Entity> {
        if (this.isMultiValue || this.values.length < 1)
            this.values.push(v);
        return this;
    }

}
