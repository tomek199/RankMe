import { Component, OnInit } from '@angular/core';
import { InfoService } from './info.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {
  apiVersion: any;

  constructor(private infoService: InfoService) { }

  ngOnInit(): void {
    this.getApiVersion()
  }

  private getApiVersion() {
    this.infoService.apiVersion().subscribe(({data}) => this.apiVersion = data.info)
  }
}
