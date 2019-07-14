import {Component} from '@angular/core';
import {TagSearchComponent} from './tag-search.component';
import {Tag} from '../model/politics/tag';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import {TagService} from '../services/tag.service';

@Component({
  selector: 'tag-search-modal',
  templateUrl: './tag-search.modal.component.html',
  styleUrls: ['./tag-search.component.scss']
})
export class TagSearchModalComponent extends TagSearchComponent {

  constructor(tagService: TagService, public activeModal: NgbActiveModal) {
    super(tagService);
  }

  select(tag: Tag) {
    return this.activeModal.close(tag);
  }

  dismiss() {
    return this.activeModal.dismiss();
  }

  close() {
    return this.dismiss();
  }

}
