import { AfterViewInit, Component, Input, ViewChild } from '@angular/core';
import {MatPaginator, MatPaginatorModule} from '@angular/material/paginator';
import {MatSort, MatSortModule, SortDirection} from '@angular/material/sort';
import {MatTableDataSource, MatTableModule} from '@angular/material/table';

@Component({
  selector: 'app-db-table',
  standalone: true,
  imports: [ MatPaginatorModule, MatSortModule, MatTableModule ],
  templateUrl: './db-table.component.html',
  styleUrl: './db-table.component.css'
})
export class DbTableComponent<T> implements AfterViewInit {
  @Input() dataSource = new MatTableDataSource<T>();
  @Input() columns: any[] = [];
  @Input() displayedColumns: string[] = [];

  @ViewChild(MatPaginator) paginator: MatPaginator | null = null;;
  @ViewChild(MatSort) sort: MatSort | null = null;;
  
  ngAfterViewInit() {
    if (this.paginator) {
      this.dataSource.paginator = this.paginator;
    }
    if (this.sort) {
      this.dataSource.sort = this.sort;
    }
  }
}
