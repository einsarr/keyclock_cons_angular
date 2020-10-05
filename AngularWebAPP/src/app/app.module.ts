import { BrowserModule } from '@angular/platform-browser';
import {APP_INITIALIZER, ApplicationRef, DoBootstrap, NgModule} from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { ProductsComponent } from './products/products.component';
import { SuppliersComponent } from './suppliers/suppliers.component';
import {KeycloakSecurityService} from './services/keycloak-security.service';
import {retry} from 'rxjs/operators';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import {RequestInterceptorService} from './services/request-interceptor.service';
/*
export function kcFactory(kcSecurity:KeycloakSecurityService) {
    return ()=>kcSecurity.init();
}*/
const securityService=new KeycloakSecurityService();

@NgModule({
  declarations: [
    AppComponent,
    ProductsComponent,
    SuppliersComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,HttpClientModule
  ],
  providers: [
    {provide:KeycloakSecurityService,useValue:securityService},
    /**{provide:APP_INITIALIZER,deps:[KeycloakSecurityService],useFactory:kcFactory,multi:true},*/
    {provide:HTTP_INTERCEPTORS,useClass:RequestInterceptorService,multi:true}
  ],
  entryComponents:[AppComponent]
  //bootstrap: [AppComponent]
})
export class AppModule implements DoBootstrap{
  ngDoBootstrap(appRef: ApplicationRef): void {
    securityService.init().then(res=>{
      console.log(res.auth);
      console.log(res.token);
      appRef.bootstrap(AppComponent);
    }).catch(err=>{
      console.log(err);
    });
  }
}
