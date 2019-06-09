import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {HttpClientModule} from '@angular/common/http';

import {AppComponent} from './app.component';
import {AppRoutingModule} from './app-routing.module';
import {DataTableModule} from 'angular-6-datatable';
import {Ng2SmartTableModule} from 'ng2-smart-table';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import { ProvinceSearchComponent } from './province-search/province-search.component';
import { TagSearchComponent } from './tag-search/tag-search.component';

@NgModule({
  declarations: [
    AppComponent,
    ProvinceSearchComponent,
    TagSearchComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpClientModule,
    AppRoutingModule,
    DataTableModule,
    Ng2SmartTableModule,
    NgbModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule {
}
