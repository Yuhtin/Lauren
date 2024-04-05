package com.yuhtin.lauren.util;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public record FutureBuilder<T>(CompletableFuture<T> future) {

    public static <U> FutureBuilder<U> of(CompletableFuture<U> task) {
        return new FutureBuilder<>(task);
    }

    public static <U> FutureBuilder<U> of(Supplier<U> value) {
        return new FutureBuilder<>(CompletableFuture.supplyAsync(value));
    }

    public static FutureBuilder<Void> empty() {
        return new FutureBuilder<>(null);
    }

    public static <U> FutureBuilder<U> allOf(FutureBuilder<?>... builders) {
        CompletableFuture<?>[] futures = new CompletableFuture[builders.length];
        for (int i = 0; i < builders.length; i++) {
            futures[i] = builders[i].future;
        }

        return new FutureBuilder<>(CompletableFuture.allOf(futures).thenApplyAsync(unused -> null));
    }

    public <F> FutureBuilder<F> thenApplyAsync(Function<T, F> apply) {
        return new FutureBuilder<>(future.thenApplyAsync(apply));
    }

    public void queue(Consumer<T> callback) {
        if (future == null) return;

        try {
            Promise.execute(() -> future.thenAcceptAsync(callback).orTimeout(15, TimeUnit.SECONDS));
        } catch (Exception exception) {
            LoggerUtil.printException(exception);
        }
    }

    public void queue() {
        if (future == null) return;

        try {
            Promise.execute(() -> future.orTimeout(15, TimeUnit.SECONDS));
        } catch (Exception exception) {
            LoggerUtil.printException(exception);
        }
    }

    public T retrieve() {
        if (future == null) return null;

        try {
            return future.get(5, TimeUnit.SECONDS);
        } catch (Exception exception) {
            LoggerUtil.printException(exception);
            return null;
        }
    }

}