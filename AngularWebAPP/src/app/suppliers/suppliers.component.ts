import { Component, OnInit } from '@angular/core';
import {SuppliersService} from '../services/suppliers.service';
import {subscribeOn} from 'rxjs/operators';

@Component({
  selector: 'app-suppliers',
  templateUrl: './suppliers.component.html',
  styleUrls: ['./suppliers.component.css']
})
export class SuppliersComponent implements OnInit {
  public suppliers:any;
  public errorMessage:any;
  constructor(private  supplierService:SuppliersService) { }

  ngOnInit(): void {
    this.supplierService.getSuppliers()
      .subscribe(data=>{
        this.suppliers=data;
      },error=>{
        this.errorMessage=error.error.message;
      });
    }

}
