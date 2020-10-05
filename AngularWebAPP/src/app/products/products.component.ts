import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-products',
  templateUrl: './products.component.html',
  styleUrls: ['./products.component.css']
})
export class ProductsComponent implements OnInit {
  products;
  constructor() { }

  ngOnInit(): void {
    this.products=[
      {id:1,name:"HP Laptop",price:45800},
      {id:2,name:"Lonovo Laptop",price:7810},
      {id:3,name:"Samsung",price:85800}
    ]
  }

}
