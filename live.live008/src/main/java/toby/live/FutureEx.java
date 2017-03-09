package toby.live;

import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.*;

/**
 * Created by khcheon on 2016-12-17.
 * Future, Callback
 */
@Slf4j
public class FutureEx {

    interface SuccessCallBack {
        void onSuccess(String result);
    }

    interface ExceptionCallBack {
        void onError(Throwable t);
    }

    public static class CallbackFutureTask extends FutureTask<String> {
        SuccessCallBack sc;
        ExceptionCallBack ec;
        public CallbackFutureTask(Callable<String> callable, SuccessCallBack sc, ExceptionCallBack ec) {
            super(callable);
            this.sc = Objects.requireNonNull(sc);
            this.ec = Objects.requireNonNull(ec);
        }

        @Override
        protected void done() {
            try {
                sc.onSuccess(get());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (ExecutionException e) {
                ec.onError(e.getCause());
            }
        }
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService es = Executors.newCachedThreadPool();

        /*
        Future<String> f = es.submit(() -> {
            Thread.sleep(2000);
            log.info("Async");
            return "Hello";
        });
        */

        /*
        FutureTask<String> f = new FutureTask<String>(()->{
            Thread.sleep(2000);
            log.info("Async");
            return "Hello";
        }){
            @Override
            protected void done() {
                try {
                    System.out.println(get());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        };*/

        CallbackFutureTask f = new CallbackFutureTask(() -> {
            Thread.sleep(2000);
            if (1 == 1) throw new RuntimeException("Async Error");
            log.info("Async");
            return "Hello";
        }
        , res -> System.out.println("result: " + res)
        , t -> System.out.println("error:" + t.getMessage()));

        es.execute(f);

//        System.out.println(f.isDone());
//        Thread.sleep(2300);
        log.info("exit"); // parallel job with f(ex)
//        System.out.println(f.isDone());
//        System.out.println(f.get()); //Blocking
        es.shutdown();
    }
}
