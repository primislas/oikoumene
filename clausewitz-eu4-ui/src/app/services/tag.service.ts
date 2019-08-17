import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {RestConstantsService} from './rest-constants.service';
import {SearchFilter} from '../model/search/filters/search.filter';
import {Observable} from 'rxjs';
import {SearchResult} from '../model/search/search.result';
import {TagListEntity} from '../model/politics/tag.list.entity';
import {TextSearchFilter} from '../model/search/filters/text.search.filter';

@Injectable({
    providedIn: 'root'
})
export class TagService {

    constructor(private http: HttpClient,
                private constants: RestConstantsService) {
    }

    searchFilters = [
        new TextSearchFilter('id', 'Tag'),
        new TextSearchFilter('name', 'Name'),
        new TextSearchFilter('primary_culture', 'Primary Culture'),
        new TextSearchFilter('religion', 'Religion'),
    ];

    searchTags(filters: SearchFilter<any>[] = []): Observable<SearchResult<TagListEntity>> {
        const params = SearchFilter.toQueryParams(filters);
        return this.http
            .get<SearchResult<TagListEntity>>(
                `${this.constants.tagSearchEndpoint}?${params}`
            );
    }

}
