import {Component, Input} from '@angular/core';
import {SearchFilter} from '../model/search.filter';
import {TagListEntity} from '../model/politics/tag.list.entity';
import {SearchDictionary} from '../model/search.dictionary';
import {noop} from 'rxjs';

@Component({
  selector: 'tag-search-table',
  templateUrl: './tag-search.table.html',
  styleUrls: ['./tag-search.component.scss']
})
export class TagSearchTableComponent {

  @Input() filters: SearchFilter[] = [];
  @Input() tags: TagListEntity[] = [];
  @Input() dict: SearchDictionary = new SearchDictionary();
  @Input() select: Function = noop;
  @Input() search: Function = noop;

}
