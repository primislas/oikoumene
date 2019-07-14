import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {RestConstantsService} from './rest-constants.service';
import {SearchFilter} from '../model/search.filter';
import {Observable} from 'rxjs';
import {SearchResult} from '../model/search.result';
import {TagListEntity} from '../model/politics/tag.list.entity';

@Injectable({
    providedIn: 'root'
})
export class TagService {

    constructor(private http: HttpClient,
                private constants: RestConstantsService) {
    }

    searchFilters = [
        new SearchFilter('name', 'Name'),
        new SearchFilter('primaryCulture', 'Primary Culture'),
        new SearchFilter('religion', 'Religion'),
    ];

    searchTags(filters: SearchFilter[] = []): Observable<SearchResult<TagListEntity>> {
        const params = filters.filter(f => f.values.length > 0)
            .map(f => f.values.map(v => `${f.id}=${v.id}`))
            .reduce((acc, a) => acc.concat(a), [])
            .join('&');
        const endpoint = (params.length > 0)
            ? `${this.constants.tagSearchEndpoint}?${params}`
            : this.constants.tagSearchEndpoint;

        return this.http
            .get<SearchResult<TagListEntity>>(endpoint)
    }

}
