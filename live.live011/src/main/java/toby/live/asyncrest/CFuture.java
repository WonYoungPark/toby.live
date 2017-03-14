package toby.live.asyncrest;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 */
@Slf4j
public class CFuture {
    public static void main(String[] args) throws ExecutionException, InterruptedException {

        ExecutorService es = Executors.newFixedThreadPool(10);
        CompletableFuture.
                supplyAsync(
                        ()-> {
                            log.info("runAsync");
                            return 1;
                        }, es)
                .thenCompose(
                        s -> {
                            log.info("thenApply {}", s);
                            return CompletableFuture.completedFuture(s + 1);
                        })
                .thenApplyAsync(
                        s2 -> {
                            log.info("thenApply {}", s2);
                            return s2 * 3;
                        }, es)
        .exceptionally(e -> 10)
        .thenAcceptAsync(s3 -> log.info("thenAccept {}", s3), es);

        log.info("exit");

        ForkJoinPool.commonPool().awaitTermination(1, TimeUnit.SECONDS);
        es.awaitTermination(1, TimeUnit.SECONDS);

        ForkJoinPool.commonPool().shutdown();;
        es.shutdown();
    }

}
