import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

public class MonoTest {

    @Test
    void firstMono() {
        Mono.just("A")
            .log()
            .subscribe();
    }

    /**
     * Subscribe() is an overloaded method.  This use involves passing a Consumer into the subscribe()
     */
    @Test
    void monoWithConsumer() {
        Mono.just("A")
                .log()
                .subscribe(s -> System.out.println(s));
    }


    @Test
    void monoWithDoOn() {
        Mono.just("A")
                .log()
                .doOnSubscribe(subs -> System.out.println("Subscribed: " + subs))
                .doOnRequest(request -> System.out.println("Request: " + request))
                .doOnSuccess(complete -> System.out.println("Complete: " + complete))
                .subscribe(System.out::println);
    }

    @Test
    void emptyMono() {
        Mono.empty()
                .log()
                .subscribe(System.out::println);
    }

    /**
     *  This call to subscribe() takes in a consumer, an error consumer ("null" in this particular case) and a complete consumer
     *  which is indicated by an anonymous function ("()") that simply invokes System.out.println("Done")
     */
    @Test
    void emptyCompleteConsumerMono() {
        Mono.empty()
                .log()
                .subscribe(System.out::println,
                        null,
                        () -> System.out.println("Done")
                );
    }

    /**
     * Checked vs Unchecked Exceptions
     */
    @Test
    void errorRuntimeExceptionMono() {
        Mono.error(new RuntimeException())
                .log()
                .subscribe();
    }

    /**
     * Checked vs Unchecked Exceptions
     */
    @Test
    void errorExceptionMono() {
        Mono.error(new Exception())
                .log()
                .subscribe();
    }

    /**
     * This call to subscribe() uses a consumer and an error consumer
     */
    @Test
    void errorConsumerMono() {
        Mono.error(new Exception())
                .log()
                .subscribe(System.out::println,
                        e -> System.out.println("Error: " + e)
                );
    }

    /**
     * The exception will cause the stream to terminate altogether.  It's basically an unhandled exception
     */
    @Test
    void errorDoOnErrorMono() {
        Mono.error(new Exception())
                .doOnError(e -> System.out.println("Error: " + e))
                .log()
                .subscribe();
    }


    /**
     * This case will allow the program to continue execution when when encountering en error
     */
    @Test
    void errorOnErrorResumeMono() {
        Mono.error(new Exception())
                .onErrorResume(e -> {
                    System.out.println("Caught: " + e);
                    return Mono.just("B");
                })
                .log()
                .subscribe();
    }

    @Test
    void errorOnErrorReturnMono() {
        Mono.error(new Exception())
                .onErrorReturn("B")
                .log()
                .subscribe();
    }
}
