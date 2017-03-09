package toby.live;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;

/**
 * Created by khcheon on 2016-12-10.
 *
 * Demon Thread: If User Thread or Main Thread are Terminated,
 *   This is also Terminated immediately
 *   Flux.interval -> timer :
 * User Thread: This is naturally terminated when its work is done.
 *
 */
@Slf4j
public class FluxScEx {

    public static void main(String[] args) throws InterruptedException {
        Flux.interval(Duration.ofMillis(500))
                .take(10)
                //.range(1, 10)
                //.publishOn(Schedulers.newSingle("pub"))
                //.log()
                //.subscribeOn(Schedulers.newSingle("sub"))
                .subscribe(s->log.debug("onNext:{}", s));

        Thread.sleep(5000L);

        System.out.println("exit");
    }

}
