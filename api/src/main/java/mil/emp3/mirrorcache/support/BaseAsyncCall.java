package mil.emp3.mirrorcache.support;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public abstract class BaseAsyncCall<Param, Result> implements AsyncCall<Param, Result> {

    final private ExecutorService executor;
    
    public BaseAsyncCall() {
        this.executor = Executors.newFixedThreadPool(5);
    }
    
    @Override
    public Future<Result> executeAsync(final Param param) {
        final Callable<Result> task = new Callable<Result>() {
            @Override public Result call() throws Exception {
                return executeSync(param);
            }
        };
        return executor.submit(task);
    }

}
