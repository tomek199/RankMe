import { ReplaySubject } from 'rxjs';
import { convertToParamMap, ParamMap, Params } from '@angular/router';
import { Injectable } from '@angular/core';

export class ActivatedRouteStub {
    private subject = new ReplaySubject<ParamMap>();

    constructor(initialParams?: Params) {
        this.setParamMap(initialParams);
    }

    readonly params = this.subject.asObservable();

    setParamMap(params: Params = {}) {
        this.subject.next(convertToParamMap(params));
    }
}

@Injectable({
  providedIn: 'root'
})
export class SnackbarServiceStub {
}
