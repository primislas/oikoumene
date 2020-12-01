import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {RestConstantsService} from './rest-constants.service';
import {Observable} from 'rxjs';
import {SearchResult} from '../model/search/search.result';
import {ProvinceListEntity} from '../model/province/province.list.entity';
import {SearchFilter} from '../model/search/filters/search.filter';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {TextSearchFilter} from '../model/search/filters/text.search.filter';

@Injectable({
  providedIn: 'root'
})
export class ProvinceService {

  searchFilters: SearchFilter<any>[] = [
    new TextSearchFilter('name', 'Name'),

    new TextSearchFilter('owner', 'Owner'),
    new TextSearchFilter('controller', 'Controller'),
    new TextSearchFilter('core', 'Core'),

    new TextSearchFilter('religion', 'Religion'),
    new TextSearchFilter('religion_group', 'Religion Group'),
    new TextSearchFilter('culture', 'Culture'),
    new TextSearchFilter('culture_group', 'Culture Group'),

    new TextSearchFilter('area', 'Area'),
    new TextSearchFilter('region', 'Region'),
    new TextSearchFilter('superregion', 'Super-region'),
    new TextSearchFilter('continent', 'Continent'),

    new TextSearchFilter('trade_good', 'Trade Good'),
    new TextSearchFilter('trade_node', 'Trade Node'),
  ];

  constructor(
    private http: HttpClient,
    private constants: RestConstantsService,
    private modalService: NgbModal
  ) {
  }

  searchProvinces(filters: SearchFilter<any>[] = []): Observable<SearchResult<ProvinceListEntity>> {
    const params = SearchFilter.toQueryParams(filters);
    return this.http
      .get<SearchResult<ProvinceListEntity>>(
        `${this.constants.provinceSearchEndpoint}?${params}`
      );
  }

}
