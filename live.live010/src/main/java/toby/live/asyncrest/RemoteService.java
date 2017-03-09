package toby.live.asyncrest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by khcheon on 2017-01-01.
 */
@SpringBootApplication
public class RemoteService {

    @RestController
    public static class MyController {
        @RequestMapping("service")
        public String service(String req) throws InterruptedException {
            Thread.sleep(2000L);
            //throw new RuntimeException();
            return req + "/service";
        }

        @RequestMapping("service2")
        public String service2(String req) throws InterruptedException {
            Thread.sleep(2000L);
            return req + "/service2";
        }
    }

    public static void main(String[] args) {
        System.setProperty("SERVER_PORT", "8081");
        System.setProperty("server.tomcat.max-threads", "1000");

        SpringApplication.run(RemoteService.class, args);
    }
}
