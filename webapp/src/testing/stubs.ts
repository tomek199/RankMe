import { ReplaySubject } from 'rxjs';
import { convertToParamMap, ParamMap, Params } from '@angular/router';

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
