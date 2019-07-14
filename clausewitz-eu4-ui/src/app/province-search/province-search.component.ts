import {Component, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {ProvinceService} from '../services/province.service';
import {ProvinceListEntity} from '../model/province/province.list.entity';
import {ProvinceGroupEntity} from '../model/province/province.group.entity';
import {SearchResult} from '../model/search.result';
import {SearchDictionary} from '../model/search.dictionary';
import {SearchFilter} from '../model/search.filter';
import {Entity} from '../model/entity';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {TagSearchModalComponent} from '../tag-search/tag-search.modal.component';

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
  filters: SearchFilter[] = [];
  pageSizes: number[] = [5,10,25,50,100,200,500,1000];

  @ViewChild('tagSearchModal')
  tagSearchModal: TemplateRef<any>;


  constructor(
      private modalService: NgbModal,
      private provinceService: ProvinceService,
  ) { }

  ngOnInit() {
    this.pagination = new SearchResult();
    this.filters = this.provinceService.searchFilters;
    this.searchProvinces();
  }

  searchProvinces(page = this.pagination.page, withDictionary = true) {
    if (page >= 1 && page <= this.pagination.totalPages)
      this.pagination.page = page;

    const pageFilter = new SearchFilter("page", "Page").addValue(new Entity(page));
    const pageSizeFilter = new SearchFilter("size", "Page Size").addValue(new Entity(this.pagination.size));
    const dictFilter = new SearchFilter("with_dictionary", "Include Dictionary").addValue(new Entity(withDictionary));

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

  removeFilterValue(f: SearchFilter, index: number) {
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

  searchTag() {
    // this.modalService
    //     .open(this.tagSearchModal)
    this.modalService
        .open(TagSearchModalComponent, {size: 'lg'})
        .result
        .then(res => {
          console.log(res);
        })
        .catch(err => {
          console.log(err);
        });
  }

}
