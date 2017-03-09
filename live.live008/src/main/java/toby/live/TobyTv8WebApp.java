package toby.live;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;

/**
 * Created by khcheon on 2016-12-17.
 *
 */
@Slf4j
@SpringBootApplication
@EnableAsync
public class TobyTv8WebApp {

    @RestController
    public static class MyController {
       @GetMapping("/callable")
         public Callable<String> callable() throws InterruptedException {
            log.info("callable");
            return () -> {
                log.info("async");
                Thread.sleep(2000);
                return "hello";
            };
        }
       /*public String callable() throws InterruptedException {
            log.info("aysnc");
            Thread.sleep(2000);
            return "hello";
        }*/
    }

    @RestController
    public static class MyDeferController {
        Queue<DeferredResult<String>> results = new ConcurrentLinkedQueue<>();

        @GetMapping("/dr")
        public DeferredResult<String> callable() throws InterruptedException {
            log.info("dr");

            DeferredResult<String> dr = new DeferredResult<>(60000L);
            results.add(dr);
            return dr;
        }

        @GetMapping("/dr/count")
        public String drcount() {
            return String.valueOf(results.size());
        }

        @GetMapping("/dr/event")
        public String drevent(String msg) {
            for (DeferredResult<String> dr: results) {
                dr.setResult("Hello" + msg);
                results.remove(dr);
            }
            return "OK";
        }
    }

    @RestController
    public static class MyEmitterController {
        Queue<DeferredResult<String>> results = new ConcurrentLinkedQueue<>();

        @GetMapping("/emitter")
        public ResponseBodyEmitter callable() throws InterruptedException {
            log.info("emitter");
            ResponseBodyEmitter emitter = new ResponseBodyEmitter();
            Executors.newSingleThreadExecutor().submit(() -> {
                for (int i = 0 ; i <= 50 ; i ++) {
                    try {
                        emitter.send("<p>stream" + i + "</p>");
                        Thread.sleep(100);
                    } catch (InterruptedException | IOException e) {
                        e.printStackTrace();
                    }
                }

            });
            return emitter;
        }
    }


    public static void main(String[] args) {
        SpringApplication.run(TobyTv8WebApp.class, args);
    }
}
