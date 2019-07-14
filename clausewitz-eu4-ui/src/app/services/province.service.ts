import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {RestConstantsService} from './rest-constants.service';
import {Observable} from 'rxjs';
import {SearchResult} from '../model/search.result';
import {ProvinceListEntity} from '../model/province/province.list.entity';
import {SearchFilter} from '../model/search.filter';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';

@Injectable({
    providedIn: 'root'
})
export class ProvinceService {

    searchFilters: SearchFilter[] = [
        new SearchFilter('name', 'Name'),

        new SearchFilter('owner', 'Owner'),
        new SearchFilter('controller', 'Controller'),
        new SearchFilter('core', 'Core'),

        new SearchFilter('religion', 'Religion'),
        new SearchFilter('religion_group', 'Religion Group'),
        new SearchFilter('culture', 'Culture'),
        new SearchFilter('culture_group', 'Culture Group'),

        new SearchFilter('area', 'Area'),
        new SearchFilter('region', 'Region'),
        new SearchFilter('superregion', 'Super-region'),
        new SearchFilter('continent', 'Continent'),

        new SearchFilter('trade_good', 'Trade Good'),
        new SearchFilter('trade_node', 'Trade Node'),
    ];

    constructor(private http: HttpClient,
                private constants: RestConstantsService,
                private modalService: NgbModal) {
    }

    searchProvinces(filters: SearchFilter[] = []): Observable<SearchResult<ProvinceListEntity>> {
        const params = filters.filter(f => f.values.length > 0)
            .map(f => f.values.map(v => `${f.id}=${v.id}`))
            .reduce((acc, a) => acc.concat(a), [])
            .join('&');
        const endpoint = (params.length > 0)
            ? `${this.constants.provinceSearchEndpoint}?${params}`
            : this.constants.provinceSearchEndpoint;

        return this.http
            .get<SearchResult<ProvinceListEntity>>(endpoint)
    }

}
