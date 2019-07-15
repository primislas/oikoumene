import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import {Selector} from './selector';

export interface Modal extends Selector {
    activeModal: NgbActiveModal;
    select: Function;
    close: Function;
    dismiss: Function;
}
