package com.yuhtin.lauren.util;


import java.util.List;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class Promise<T> {


    private static final ExecutorService executor = Executors.newFixedThreadPool(128);

    private final FutureTask<T> future;

    private Promise(FutureTask<T> future) {
        this.future = future;
    }

    public static void execute(Runnable task) {
        executor.execute(task);
    }

    public static <U> Promise<U> supply(FutureTask<U> task) {
        return new Promise<>(task);
    }

    public static <U> Promise<U> supply(Callable<U> task) {
        FutureTask<U> future = new FutureTask<>(task);
        return new Promise<>(future);
    }

    public static Promise<Void> supply(Runnable task) {
        FutureTask<Void> future = new FutureTask<>(task, Void.TYPE.cast(null));
        return new Promise<>(future);
    }

    public static <T> Promise<Void> all(List<Promise<T>> promises) {
        return supply(() -> {
            for (Promise promise : promises) {
                try {
                    promise.future.get();
                } catch (InterruptedException | ExecutionException exception) {
                    LoggerUtil.printException(exception);
                }
            }

            return null;
        });
    }

    public void queue(Consumer<T> callback) {
        if (future != null) {
            executor.submit(() -> {
                try {
                    callback.accept(future.get(30, TimeUnit.SECONDS));
                } catch (InterruptedException | ExecutionException | TimeoutException exception) {
                    LoggerUtil.printException(exception);
                }
            });
        }
    }

    public void then(Runnable callback) {
        if (future != null) {
            executor.submit(() -> {
                try {
                    future.get(30, TimeUnit.SECONDS);
                    callback.run();
                } catch (InterruptedException | ExecutionException | TimeoutException exception) {
                    LoggerUtil.printException(exception);
                }
            });
        }
    }
}