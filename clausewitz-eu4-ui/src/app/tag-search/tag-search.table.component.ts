import {Component, Input} from '@angular/core';
import {SearchFilter} from '../model/search/filters/search.filter';
import {Paginateable} from '../pagination/paginateable';
import {Selector} from '../utils/selector';

@Component({
  selector: 'tag-search-table',
  templateUrl: './tag-search.table.html',
  styleUrls: ['./tag-search.component.scss']
})
export class TagSearchTableComponent {

  @Input() page: Paginateable;
  @Input() filters: SearchFilter<any>[] = [];
  @Input() selector: Selector;

}
