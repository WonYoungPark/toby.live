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

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by khcheon on 2017-01-01.
 * 토비의 봄 TV 10화 -
 */
@SpringBootApplication
public class TobyTV010Application {

    @RestController
    public static class MyController {
        int nioEventLoopCnt = 1;
        AsyncRestTemplate rt = new AsyncRestTemplate(
                new Netty4ClientHttpRequestFactory(new NioEventLoopGroup(nioEventLoopCnt)));
        String URL1 = "http://localhost:8081/service?req={req}";
        String URL2 = "http://localhost:8081/service2?req={req}";
        @Autowired MyService myService;

        boolean useFunction = true;

        @GetMapping("rest")
        public DeferredResult<String> rest(int idx) {
            DeferredResult<String> dr = new DeferredResult<>();

            if (!useFunction)
            {
                ListenableFuture<ResponseEntity<String>> f1 = rt.getForEntity(URL1, String.class, "hello" + idx);
                f1.addCallback((ResponseEntity<String> s) -> {
                    ListenableFuture<ResponseEntity<String>> f2 = rt.getForEntity(URL2, String.class, s.getBody());
                    f2.addCallback((ResponseEntity<String> s2) -> {

                        ListenableFuture<String> f3 = myService.work(s2.getBody());
                        f3.addCallback(s3-> dr.setResult(s3)
                        , e-> dr.setErrorResult(e.getMessage())
                        );
                    }, e-> dr.setErrorResult(e.getMessage()));
                }, e->dr.setErrorResult(e.getMessage()));
            }

            if (useFunction)
            {
                Completion.from(rt.getForEntity(URL1, String.class, "hello" + idx))
                        .andApply(s -> rt.getForEntity(URL2, String.class, s.getBody()))
                        .andApply(s -> myService.work(s.getBody()))
                        .andError(e -> dr.setErrorResult(e.toString()))
                        .addAccept(dr::setResult);
            }
            return dr;
        }
    }

    static class AcceptCompletion<S> extends Completion<S, Void> {
        private Consumer<S> con;
        AcceptCompletion(Consumer<S> con) {
            this.con = con;
        }

        @Override
        void run(S value) {
            con.accept(value);
        }
    }

    static class ApplyCompletion<S, T> extends Completion<S, T> {
        private Function<S, ListenableFuture<T>> fn;
        ApplyCompletion(Function<S, ListenableFuture<T>> fn) {
            this.fn = fn;
        }

        @Override
        void run(S value) {
            ListenableFuture<T> lf = fn.apply(value);
            lf.addCallback(this::complete, this::error);
        }
    }

    static class ErrorCompletion<T> extends Completion<T, T> {
        private Consumer<Throwable> th;
        ErrorCompletion(Consumer<Throwable> fn) {
            this.th = fn;
        }

        @Override
        void run(T value) {
            if (next != null) next.run(value);
        }

        @Override
        void error(Throwable e) {
            th.accept(e);
        }
    }

    static class Completion<S,T> {
        Completion next;

        void addAccept(Consumer<T> con) {
            Completion<T, Void> c = new AcceptCompletion<>(con);
            this.next = c;
        }

        Completion<T, T> andError(Consumer<Throwable> econ) {
            Completion<T, T> c = new ErrorCompletion<>(econ);
            this.next = c;
            return c;
        }

        <V> Completion<T, V> andApply(Function<T, ListenableFuture<V>> fn) {
            Completion<T, V> c = new ApplyCompletion<>(fn);
            this.next = c;
            return c;
        }

        static <S, T> Completion<S, T> from(ListenableFuture<T> lf) {
            Completion<S, T> c = new Completion<>();
            lf.addCallback(c::complete, c::error);
            return c;
        }

        void error(Throwable e) {
            if (next!= null) next.error(e);
        }

        void complete(T s) {
            if (next != null)
                next.run(s);
        }

        void run(S value){
        }
    }

    @Service
    static class MyService {
        ListenableFuture<String> work(String param) {
            return new AsyncResult<>(param + "/work");
        }
    }

    @Bean
    ThreadPoolTaskExecutor tbExecutor() {
        ThreadPoolTaskExecutor tb = new ThreadPoolTaskExecutor();
        tb.setCorePoolSize(1);
        tb.setMaxPoolSize(1);
        tb.initialize();
        return tb;
    }

    public static void main(String[] args) {
        SpringApplication.run(TobyTV010Application.class, args);
    }
}
