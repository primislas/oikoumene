import { Component, OnInit } from '@angular/core';
import {ProvinceService} from '../services/province.service';
import {ProvinceListEntity} from '../model/province/province.list.entity';
import {ProvinceGroupEntity} from '../model/province/province.group.entity';
import {SearchResult} from '../model/search.result';
import {SearchDictionary} from '../model/search.dictionary';
import {ProvinceSearchFilter} from './province.search.filter';
import {Entity} from '../model/entity';

@Component({
  selector: 'app-province-search',
  templateUrl: './province-search.component.html',
  styleUrls: ['./province-search.component.scss']
})
export class ProvinceSearchComponent implements OnInit {

  pagination: SearchResult<any>;
  provinces: ProvinceListEntity[];
  groups: ProvinceGroupEntity[];
  dict: SearchDictionary = new SearchDictionary();
  filters: ProvinceSearchFilter[] = [];
  pageSizes: number[] = [5,10,25,50,100,200,500,1000];


  constructor(
      private provinceService: ProvinceService
  ) { }

  ngOnInit() {
    this.pagination = new SearchResult();
    this.filters = this.provinceService.searchFilters;
    this.searchProvinces();
  }

  searchProvinces(page = this.pagination.page, withDictionary = true) {
    if (page >= 1 && page <= this.pagination.totalPages)
      this.pagination.page = page;

    const pageFilter = new ProvinceSearchFilter("page", "Page").addValue(new Entity(this.pagination.page));
    const pageSizeFilter = new ProvinceSearchFilter("size", "Page Size").addValue(new Entity(this.pagination.size));
    const dictFilter = new ProvinceSearchFilter("with_dictionary", "Include Dictionary").addValue(new Entity(withDictionary));

    const allFilters = [pageFilter, pageSizeFilter, dictFilter].concat(this.filters);

    this.provinceService
        .searchProvinces(allFilters)
        .subscribe(res => {
          this.provinces = res.entities;
          this.pagination = res;
          this.dict = res.dictionary;
        });
  }

  editProvince(p: ProvinceListEntity) {

  }

  removeFilterValue(f: ProvinceSearchFilter, index: number) {
    f.removeValue(index);
    this.searchProvinces();
  }

  first() {
    this.searchProvinces(1);
  }

  prev() {
    this.searchProvinces(this.pagination.page - 1);
  }

  next() {
    this.searchProvinces(this.pagination.page + 1);
  }

  last() {
    this.searchProvinces(this.pagination.totalPages);
  }

}
