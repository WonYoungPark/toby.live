package toby.live.asyncrest;

import io.netty.channel.nio.NioEventLoopGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.Netty4ClientHttpRequestFactory;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureTask;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by khcheon on 2017-01-01.
 * 토비의 봄 TV 9화 -
 */
@SpringBootApplication
public class Tobytv009Application {
    @RestController
    public static class MyController {
//        RestTemplate rt = new RestTemplate();
//        @RequestMapping("rest")
//        public String rest(int idx) {
//            String res = rt.getForObject("http://localhost:8081/service?req={req}", String.class, "hello" + idx);
//
//            return "rest" + idx + "::" + res;
//        }

//        AsyncRestTemplate rt = new AsyncRestTemplate(); // make SimpleAsyncThreads...
//        AsyncRestTemplate rt = new AsyncRestTemplate(new Netty4ClientHttpRequestFactory(new NioEventLoopGroup(1)));
//        @GetMapping("rest")
//        public ListenableFuture<ResponseEntity<String>> rest(int idx) {
//            ListenableFuture<ResponseEntity<String>> res
//                    = rt.getForEntity("http://localhost:8081/service?req={req}", String.class, "hello" + idx);
//
//            return res;
//        }

//        int nioEventLoopCnt = 1;
//        AsyncRestTemplate rt = new AsyncRestTemplate(
//                new Netty4ClientHttpRequestFactory(new NioEventLoopGroup(nioEventLoopCnt)));
//        @GetMapping("rest")
//        public DeferredResult<String> rest(int idx) {
//            DeferredResult<String> dr = new DeferredResult<>();
//            ListenableFuture<ResponseEntity<String>> res
//                    = rt.getForEntity("http://localhost:8081/service?req={req}", String.class, "hello" + idx);
//
//            res.addCallback(s -> dr.setResult(s.getBody() + "/work")
//                    , e -> dr.setErrorResult(e.getMessage()));
//            return dr;
//        }

        int nioEventLoopCnt = 1;
        AsyncRestTemplate rt = new AsyncRestTemplate(
                new Netty4ClientHttpRequestFactory(new NioEventLoopGroup(nioEventLoopCnt)));
        String URL = "http://localhost:8081/service?req={req}";
        @Autowired MyService myService;

        @GetMapping("rest")
        public DeferredResult<String> rest(int idx) {

            DeferredResult<String> dr = new DeferredResult<>();
            ListenableFuture<ResponseEntity<String>> f1
                    = rt.getForEntity(URL, String.class, "hello" + idx);
            f1.addCallback(s1 -> {
                        ListenableFuture<ResponseEntity<String>> f2 = rt.getForEntity(
                                "http://localhost:8081/service2?req={req}"
                                , String.class, s1.getBody());
                        f2.addCallback(s2 -> {
                                    ListenableFuture<String> f3 = myService.work(s2.getBody() + "/work");
                                    f3.addCallback(s3 -> dr.setResult(s3)
                                            , e3 -> dr.setErrorResult(e3.getMessage())
                                    );
                                }
                                , e2 -> dr.setErrorResult(e2.getMessage()));
                    }
                    , e -> dr.setErrorResult(e.getMessage()));
            return dr;
        }
    }

    @Service
    static class MyService {
        ListenableFuture<String> work(String param) {
            return new AsyncResult<>(param + "/service");
        }
    }

    @Bean
    ThreadPoolTaskExecutor tbExecutor() {
        ThreadPoolTaskExecutor tb = new ThreadPoolTaskExecutor();
        tb.setThreadGroupName("bbbbbbbbb");
        tb.setThreadNamePrefix("aaaaaaaaaaaa");
        tb.setCorePoolSize(1);
        tb.setMaxPoolSize(1);
        tb.initialize();
        return tb;
    }

    public static void main(String[] args) {
        SpringApplication.run(Tobytv009Application.class, args);
    }
}
