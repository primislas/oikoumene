import {Component, OnInit} from '@angular/core';
import {SearchResult} from '../model/search.result';
import {TagSearchFilter} from './tag.search.filter';
import {Entity} from '../model/entity';
import {SearchDictionary} from '../model/search.dictionary';
import {TagService} from '../services/tag.service';

@Component({
  selector: 'app-tag-search',
  templateUrl: './tag-search.component.html',
  styleUrls: ['./tag-search.component.scss']
})
export class TagSearchComponent implements OnInit {

  pagination: SearchResult<any>;
  filters: TagSearchFilter[] = [];
  pageSizes: number[] = [5,10,25];
  tags: any[] = [];
  dict: SearchDictionary;


  constructor(
      private tagService: TagService
  ) { }

  ngOnInit() {
  }


  searchTags(page = this.pagination.page, withDictionary = true) {
    if (page >= 1 && page <= this.pagination.totalPages)
      this.pagination.page = page;

    const pageFilter = new TagSearchFilter("page", "Page").addValue(new Entity(this.pagination.page));
    const pageSizeFilter = new TagSearchFilter("size", "Page Size").addValue(new Entity(this.pagination.size));
    const dictFilter = new TagSearchFilter("with_dictionary", "Include Dictionary").addValue(new Entity(withDictionary));

    const allFilters = [pageFilter, pageSizeFilter, dictFilter].concat(this.filters);

    this.tagService
        .searchTags(allFilters)
        .subscribe(res => {
          this.tags = res.entities;
          this.pagination = res;
          this.dict = res.dictionary;
        });
  }

}
