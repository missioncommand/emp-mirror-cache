package mil.emp3.mirrorcache.support;

import java.util.concurrent.Future;

import mil.emp3.mirrorcache.MirrorCacheException;

public interface AsyncCall<Param, Result> {
    
    Result executeSync(Param param) throws MirrorCacheException;
    Future<Result> executeAsync(Param param);

}
