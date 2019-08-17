import {Component, Input, OnInit} from '@angular/core';
import {PaginationConf} from './pagination.conf';
import {SearchResult} from '../model/search/search.result';
import {Paginateable} from './paginateable';

@Component({
    selector: 'pagination',
    templateUrl: './pagination.component.html',
    styleUrls: ['./pagination.component.scss']
})
export class PaginationComponent implements OnInit, Paginateable {

    ngOnInit() {
    }

    @Input() conf: Paginateable = this;

    pagination: PaginationConf = new PaginationConf();
    searchResult: SearchResult<any> = new SearchResult();
    pageSizes: number[] = [5, 10, 25, 50, 100];

    search(): void {
        this.conf.search();
    }

    first(): void {
        this.conf.first();
    }

    last(): void {
        this.conf.last();
    }

    next(): void {
        this.conf.next();
    }

    prev(): void {
        this.conf.prev();
    }

}
