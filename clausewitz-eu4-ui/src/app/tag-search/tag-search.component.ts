import {Component, OnInit} from '@angular/core';
import {SearchResult} from '../model/search.result';
import {SearchFilter} from '../model/search.filter';
import {Entity} from '../model/entity';
import {SearchDictionary} from '../model/search.dictionary';
import {TagService} from '../services/tag.service';
import {TagListEntity} from '../model/politics/tag.list.entity';
import {Paginateable} from '../pagination/paginateable';
import {PaginationConf} from '../pagination/pagination.conf';

@Component({
  selector: 'tag-search',
  templateUrl: './tag-search.component.html',
  styleUrls: ['./tag-search.component.scss']
})
export class TagSearchComponent implements OnInit, Paginateable {

  self() {
    return this;
  }

  pageSizes: number[] = [5,10,25];
  searchResult: SearchResult<TagListEntity> = new SearchResult();
  pagination: PaginationConf = new PaginationConf(this.searchTags, this.first, this.next, this.prev, this.last);

  filters: SearchFilter[] = [];
  tags: TagListEntity[] = [];
  dict: SearchDictionary = new SearchDictionary();


  constructor(
      private tagService: TagService
  ) { }

  ngOnInit() {
    this.filters = this.tagService.searchFilters;
    this.searchTags();
  }

  searchTags(page = this.searchResult.page, withDictionary = true) {
    if (page >= 1 && page <= this.searchResult.totalPages)
      this.searchResult.page = page;

    const pageFilter = new SearchFilter("page", "Page").addValue(new Entity(page - 1));
    const pageSizeFilter = new SearchFilter("size", "Page Size").addValue(new Entity(this.searchResult.size));
    const dictFilter = new SearchFilter("with_dictionary", "Include Dictionary").addValue(new Entity(withDictionary));

    const allFilters = [pageFilter, pageSizeFilter, dictFilter].concat(this.filters);

    this.tagService
        .searchTags(allFilters)
        .subscribe(res => {
          this.tags = res.entities;
          this.searchResult = res;
          this.dict = res.dictionary;
        });
  }

  search() {
    this.searchTags();
  }

  first() {
    this.searchTags(1);
  }

  prev() {
    this.searchTags(this.searchResult.page - 1);
  }

  next() {
    this.searchTags(this.searchResult.page + 1);
  }

  last() {
    this.searchTags(this.searchResult.totalPages);
  }

}
