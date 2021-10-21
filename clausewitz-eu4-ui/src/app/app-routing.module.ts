import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {ProvinceSearchComponent} from './province-search/province-search.component';
import {MapComponent} from "./map/map.component";
import {MapThreeDComponent} from "./map-three-d/map-three-d.component";

const routes: Routes = [
  {path: '', redirectTo: '/provinces', pathMatch: 'full'},
  {path: 'provinces', component: ProvinceSearchComponent},
  {path: 'world-map-svg', component: MapComponent},
  {path: 'world-map', component: MapThreeDComponent},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}

