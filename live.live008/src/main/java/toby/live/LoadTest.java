package toby.live;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by khcheon on 2016-12-17.
 */
@Slf4j
public class LoadTest {
    static AtomicInteger counter = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {
        ExecutorService ex = Executors.newFixedThreadPool(100);
        RestTemplate rt = new RestTemplate();
        String url = "http://localhost:8080/rest?idx=1";
        //String url = "http://localhost:8080/callable";
        StopWatch main = new StopWatch();
        main.start();

        for (int i = 0 ; i < 100 ; i ++) {
            ex.execute(()->{
                int idx = counter.incrementAndGet();
                log.info("Thread:{}", idx);

                StopWatch sw = new StopWatch();
                sw.start();
                rt.getForObject(url, String.class);
                sw.stop();
                log.info("Elapse: {}->{}",idx, + sw.getTotalTimeSeconds());
            });
        }

        ex.shutdown();
        ex.awaitTermination(100, TimeUnit.SECONDS);

        main.stop();
        log.info("total:{}", main.getTotalTimeSeconds());
    }
}
