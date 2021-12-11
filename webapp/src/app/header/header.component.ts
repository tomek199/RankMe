import { Component, OnInit } from '@angular/core';
import { VersionService } from './version.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {
  apiVersion: any;

  constructor(private versionService: VersionService) { }

  ngOnInit(): void {
    this.getApiVersion()
  }

  private getApiVersion() {
    this.versionService.apiVersion().subscribe(({data}) => this.apiVersion = data.version)
  }
}
