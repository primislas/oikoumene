import {SearchDictionary} from './search.dictionary';

export class SearchResult<T> {
    page: number = 1;
    size: number = 10;
    totalPages: number = 1;
    totalEntities: number = 0;
    entities: T[] = [];
    dictionary: SearchDictionary = new SearchDictionary();


    constructor(page: number = 1, size: number = 10, totalPages: number = 1, totalEntities: number = 0, entities: T[] = [], dictionary: SearchDictionary = new SearchDictionary()) {
        this.page = page;
        this.size = size;
        this.totalPages = totalPages;
        this.totalEntities = totalEntities;
        this.entities = entities;
        this.dictionary = dictionary;
    }

    static fromJsonObj(obj) {
        return Object.assign(new SearchResult(), obj);
    }
}
