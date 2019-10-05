package com.itzap.rxjava.command;

import com.itzap.common.ErrorCode;
import com.itzap.common.Named;
import com.itzap.common.utils.IZapExceptionUtils;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public abstract class AbstractCommand<T, C extends AbstractCommand> implements Named {
    private static Logger LOG = LoggerFactory.getLogger(AbstractCommand.class);

    private final String name;

    public AbstractCommand(String name) {
        this.name = name;
    }

    protected abstract C getThis();

    protected abstract T run();

    @Override
    public String getName() {
        return name;
    }

    private T runCommand() {
        long mils = System.nanoTime();
        try {
            return run();
        } catch (Exception ex) {
            throw IZapExceptionUtils.propagate(ex);
        } finally {
            LOG.info("command='{}', runtime={}",
                    this.name,
                    TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - mils));
        }
    }

    public Observable<T> toObservable() {
        return Observable.unsafeCreate(subscriber -> {
            try {
                subscriber.onNext(runCommand());
                subscriber.onComplete();
            } catch (Throwable ex) {
                ErrorCode errorCode = IZapExceptionUtils.getErrorCode(ex);
                LOG.error("command='{}', errorCode='{}', errorMessage='{}'",
                        name,
                        errorCode.getErrorCode(), errorCode.getMessage(),
                        ex);
                subscriber.onError(ex);
            }
        });
    }

    public Observable<T> toObservable(Executor executor) {
        return toObservable()
                .subscribeOn(Schedulers.from(executor));
    }

    public Completable toCompletable() {
        return Completable.unsafeCreate(subscriber -> {
            try {
                runCommand();
                subscriber.onComplete();
            } catch (Throwable ex) {
                ErrorCode errorCode = IZapExceptionUtils.getErrorCode(ex);
                LOG.error("command='{}', errorCode='{}', errorMessage='{}'",
                        name,
                        errorCode.getErrorCode(), errorCode.getMessage(),
                        ex);
                subscriber.onError(ex);
            }
        });
    }

    public Completable toCompletable(Executor executor) {
        return toCompletable()
                .subscribeOn(Schedulers.from(executor));
    }
}
