package toby.live;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

/**
 * Created by khcheon on 2016-12-17.
 * FutureEx Spring version = @Async
// */
//@Slf4j
//@SpringBootApplication
//@EnableAsync
public class TobyTv8Application {
//    @Component
//    public static class MyService {
//        @Async
//        public ListenableFuture<String> hello() throws InterruptedException {
//            log.info("hello()");
//            Thread.sleep(2000);
//            return new AsyncResult<>("hello");
//        }
//    }
//
//    public static void main(String[] args) {
//        try (ConfigurableApplicationContext c = SpringApplication.run(TobyTv8Application.class, args)) {
//        }
//    }
//
//    @Bean ThreadPoolTaskExecutor tp() {
//        ThreadPoolTaskExecutor te = new ThreadPoolTaskExecutor();
//        te.setCorePoolSize(10);
//        te.setMaxPoolSize(100);
//        return te;
//    }
//
//    @Autowired MyService myService;
//
//    @Bean
//    ApplicationRunner run() {
//        return args -> {
//            log.info("run()");
//            ListenableFuture<String> f = myService.hello();
//
//            //log.info("exit:" + f.isDone());
//            //log.info("result:" + f.get());
//            f.addCallback(s -> System.out.println(s), e -> System.out.println(e.getMessage()));
//            log.info("exit");
//        };
//    }
}
