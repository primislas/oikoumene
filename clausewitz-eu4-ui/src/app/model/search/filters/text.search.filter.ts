import {SearchFilter} from './search.filter';
import {SearchFilterType} from './search.filter.type';
import {SearchFilterDisplayType} from './search.filter.display.type';
import {Observable} from 'rxjs';
import {Entity} from '../../entity';
import {SearchedValue, TextValue} from './searched.value';

export class TextSearchFilter extends SearchFilter<TextValue> {

    constructor(
        id: string,
        name: string,
        isMultiValue: Boolean = false,
        img: (T) => String = undefined,
        searchModal: () => Observable<Entity[]> = undefined,
    ) {
        super(id, name, SearchFilterType.Field, SearchFilterDisplayType.TextInput, isMultiValue, [], img, searchModal);
    }

    addValue(): SearchFilter<TextValue> {
        if (this.isMultiValue || this.values.length < 1)
            this.values.push(new SearchedValue<string>());

        const isField = super.isField();
        const isEntity = super.isEntity();
        return this;
    }

}
