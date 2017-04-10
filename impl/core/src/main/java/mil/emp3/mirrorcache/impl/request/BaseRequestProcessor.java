package mil.emp3.mirrorcache.impl.request;

import mil.emp3.mirrorcache.RequestProcessor;
import mil.emp3.mirrorcache.support.BaseAsyncCall;

public abstract class BaseRequestProcessor<Param, Result> extends BaseAsyncCall<Param, Result> implements RequestProcessor<Param, Result> {

}
