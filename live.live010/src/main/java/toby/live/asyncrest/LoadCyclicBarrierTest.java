package toby.live.asyncrest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by khcheon on 2017-01-06.
 */
@Slf4j
public class LoadCyclicBarrierTest {
    static AtomicInteger counter = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException, BrokenBarrierException {
        ExecutorService ex = Executors.newFixedThreadPool(100);

        RestTemplate rt = new RestTemplate();
        String url = "http://localhost:8080/rest?idx={idx}";

        CyclicBarrier barrier = new CyclicBarrier(101);

        for (int i = 0 ; i < 100 ; i ++) {
            ex.submit(()->{
                int idx = counter.incrementAndGet();

                barrier.await();

                log.info("Thread:{}", idx);

                StopWatch sw = new StopWatch();
                sw.start();
                String res = rt.getForObject(url, String.class, idx);
                sw.stop();
                log.info("Elapse: {}->{} / {}", idx, +sw.getTotalTimeSeconds(), res);
                return null;// for callable interface impl lambda.
            });
        }

        barrier.await();
        StopWatch main = new StopWatch();
        main.start();

        ex.shutdown();
        ex.awaitTermination(100, TimeUnit.SECONDS);

        main.stop();
        log.info("total:{}", main.getTotalTimeSeconds());

    }
}
