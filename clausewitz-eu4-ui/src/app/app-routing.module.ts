import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {ProvinceSearchComponent} from './province-search/province-search.component';

const routes: Routes = [
  {path: '', redirectTo: '/provinces', pathMatch: 'full'},
  {path: 'provinces', component: ProvinceSearchComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}

