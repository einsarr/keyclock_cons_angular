import { Injectable } from '@angular/core';
import {KeycloakInstance} from "keycloak-js";
declare var Keyclock:any;
@Injectable({
  providedIn: 'root'
})
export class KeycloakSecurityService {
  public kc;
  constructor() { }

  init(){
    return new Promise((resolve,reject)=>{
      ///console.log("Security initialisation....");
      // @ts-ignore
      this.kc=new Keycloak({
        url:"http://localhost:8080/auth",
        realm:"ecom-realm",
        clientId:"AngularProductsApp"
      });

      this.kc.init({
        //onLoad:"login-required"
        onLoad:"check-sso",
        // @ts-ignore
        promiseType : 'native'
      }).then((authenticated)=>{
        //console.log(authenticated);
        //console.log(this.kc.token);
        resolve({auth:authenticated,token:this.kc.token});
      }).catch(err=>{
        reject(err);
      });
    });

  }
}
