import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {ProvinceSearchComponent} from './province-search/province-search.component';
import {MapComponent} from "./map/map.component";

const routes: Routes = [
  {path: '', redirectTo: '/provinces', pathMatch: 'full'},
  {path: 'provinces', component: ProvinceSearchComponent},
  {path: 'world-map', component: MapComponent},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}

