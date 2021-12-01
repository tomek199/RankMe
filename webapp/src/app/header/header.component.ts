import { Component, OnInit } from '@angular/core';
import { Apollo, gql } from "apollo-angular";

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {
  apiVersion: any;
  leagueId: string = 'JYxBPfIJIl7NOcGQeU68D';

  constructor(private apollo: Apollo) {
  }

  ngOnInit(): void {
    this.apollo.query<any>({
      query: gql`{info}`
    }).subscribe(({data}) => this.apiVersion = data.info);
  }
}
