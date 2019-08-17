import {noop} from 'rxjs';

export class PaginationConf {
    search: Function;
    first: Function;
    next: Function;
    prev: Function;
    last: Function;

    constructor(
        search: Function = noop,
        first: Function = noop,
        next: Function = noop,
        prev: Function = noop,
        last: Function = noop) {

        this.search = search;
        this.first = first;
        this.next = next;
        this.prev = prev;
        this.last = last;
    }
}
