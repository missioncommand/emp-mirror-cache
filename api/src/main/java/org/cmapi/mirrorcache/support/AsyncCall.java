package org.cmapi.mirrorcache.support;

import java.util.concurrent.Future;

import org.cmapi.mirrorcache.MirrorCacheException;

public interface AsyncCall<Param, Result> {
    
    Result executeSync(Param param) throws MirrorCacheException;
    Future<Result> executeAsync(Param param);

}
