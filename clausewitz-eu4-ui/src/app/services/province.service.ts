import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {RestConstantsService} from './rest-constants.service';
import {Observable} from 'rxjs';
import {SearchResult} from '../model/search.result';
import {ProvinceListEntity} from '../model/province/province.list.entity';
import {ProvinceSearchFilter} from '../province-search/province.search.filter';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';

@Injectable({
    providedIn: 'root'
})
export class ProvinceService {

    searchFilters: ProvinceSearchFilter[] = [
        new ProvinceSearchFilter('name', 'Name'),

        new ProvinceSearchFilter('owner', 'Owner'),
        new ProvinceSearchFilter('controller', 'Controller'),
        new ProvinceSearchFilter('core', 'Core'),

        new ProvinceSearchFilter('religion', 'Religion'),
        new ProvinceSearchFilter('religion_group', 'Religion Group'),
        new ProvinceSearchFilter('culture', 'Culture'),
        new ProvinceSearchFilter('culture_group', 'Culture Group'),

        new ProvinceSearchFilter('area', 'Area'),
        new ProvinceSearchFilter('region', 'Region'),
        new ProvinceSearchFilter('superregion', 'Super-region'),
        new ProvinceSearchFilter('continent', 'Continent'),

        new ProvinceSearchFilter('trade_good', 'Trade Good'),
        new ProvinceSearchFilter('trade_node', 'Trade Node'),
    ];

    constructor(private http: HttpClient,
                private constants: RestConstantsService,
                private modalService: NgbModal) {
    }

    searchProvinces(filters: ProvinceSearchFilter[] = []): Observable<SearchResult<ProvinceListEntity>> {
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
