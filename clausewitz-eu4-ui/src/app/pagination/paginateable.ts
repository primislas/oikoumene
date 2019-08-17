import {SearchResult} from '../model/search/search.result';
import {PaginationConf} from './pagination.conf';

export interface Paginateable {
    pageSizes: number[];
    pagination: PaginationConf;
    searchResult: SearchResult<any>;

    search: Function;
    first: Function;
    next: Function;
    prev: Function;
    last: Function;

}
