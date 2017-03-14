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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by khcheon on 2017-01-01.
 * 토비의 봄 TV 11화 - CompletableFuture
 */
@SpringBootApplication
public class TobyTV011Application {

    @RestController
    public static class MyController {
        int nioEventLoopCnt = 1;
        AsyncRestTemplate rt = new AsyncRestTemplate(
                new Netty4ClientHttpRequestFactory(new NioEventLoopGroup(nioEventLoopCnt)));
        String URL1 = "http://localhost:8081/service?req={req}";
        String URL2 = "http://localhost:8081/service2?req={req}";
        @Autowired MyService myService;

        static <T> CompletableFuture<T> toCF(ListenableFuture<T> lf)
        {
            CompletableFuture<T> cf = new CompletableFuture<>();
            lf.addCallback(s -> cf.complete(s), e->cf.completeExceptionally(e));
            return cf;
        }

        @GetMapping("rest")
        public DeferredResult<String> rest(int idx) {
            DeferredResult<String> dr = new DeferredResult<>();

            toCF(rt.getForEntity(URL1, String.class, "hello" + idx))
                    .thenCompose(s -> toCF(rt.getForEntity(URL2, String.class, s.getBody())))
                    //.thenCompose(s2-> toCF(myService.work(s2.getBody())))
                    .thenApplyAsync(s2-> myService.workBlock(s2.getBody()))
                    .thenAccept(s3->dr.setResult(s3))
                    .exceptionally(e->{ dr.setErrorResult(e.getMessage());return (Void)null;});

            return dr;
        }
    }

    @Service
    static class MyService {
        ListenableFuture<String> work(String param) {
            return new AsyncResult<>(param + "/work");
        }

        String workBlock(String param) {
            return param + "/work";
        }
    }


    public static void main(String[] args) {
        SpringApplication.run(TobyTV011Application.class, args);
    }
}
