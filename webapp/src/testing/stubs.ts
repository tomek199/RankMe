import { BehaviorSubject } from 'rxjs';
import { Injectable } from '@angular/core';

export class ActivatedRouteStub {
  private subject = new BehaviorSubject(this.testParams);
  private _testParams: {};
  params = this.subject.asObservable();

  get testParams() {
    return this._testParams;
  }

  set testParams(params: {}) {
    this._testParams = params;
    this.subject.next(params);
  }
}

@Injectable({
  providedIn: 'root'
})
export class SnackbarServiceStub {
}
