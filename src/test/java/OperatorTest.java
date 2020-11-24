import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

public class OperatorTest {

    /**
     * Apply a single transformation on each element of the flux
     */
    @Test
    void map() {
        Flux.range(1, 5)
                .map(i -> i * 10)
                .subscribe(System.out::println);
    }

    /**
     * If we just .map()'ed on each element in the Flux.Range(), it would simply pass a FluxRange object to the next stage.  Since we flapMap() it,
     * each item in the _second_ flux range is is a CONSUMER of the elements emited by the first flux.  We are flattening out nested elements.
     */
    @Test
    void flatMap() {
        Flux.range(1, 5)
                .flatMap(i -> Flux.range(i*10, 2))
                .subscribe(System.out::println);
    }

    /*
    This is an example of chaining a pipeline of elements.  The result is the string that has taken in the elements emitted by the second flux.  These elements, in turn,  are the result of items emitted from the first flux.
     */
    @Test
    void flatMapMultipleStages() {
        Flux.range(1, 5)
            .flatMap(i -> Flux.range(i*10, 2))
            .flatMap(i -> Flux.just("The element I received is: ".concat(i.toString())))
            .subscribe(System.out::println);
    }

    @Test
    void flatMapMany() {
        Mono.just(3)
                .flatMapMany(i -> Flux.range(1, i))
                .subscribe(System.out::println);
    }

    @Test
    void concat() throws InterruptedException {
        Flux<Integer> oneToFive = Flux.range(1, 5)
                .delayElements(Duration.ofMillis(200));
        Flux<Integer> sixToTen = Flux.range(6, 5)
                .delayElements(Duration.ofMillis(400));

        Flux.concat(oneToFive, sixToTen)
                .subscribe(System.out::println);

//        oneToFive.concatWith(sixToTen)
//              .subscribe(System.out::println);

        Thread.sleep(4000);
    }

    @Test
    void merge() throws InterruptedException  {
        Flux<Integer> oneToFive = Flux.range(1, 5)
                .delayElements(Duration.ofMillis(200));
        Flux<Integer> sixToTen = Flux.range(6, 5)
                .delayElements(Duration.ofMillis(600));

        /*
        The merging happens in the order in which the elements in each Flux arrive.  Everything gets
        merged into the same stream, as opposed to zip() which keeps the elements from each Flux (or Mono) separate, and then you can do further processing on on each 'container' merge() has no container
         */
        Flux.merge(oneToFive, sixToTen)
                .subscribe(System.out::println);

//        oneToFive.mergeWith(sixToTen)
//                .subscribe(System.out::println);

        Thread.sleep(4000);
    }

    @Test
    void zip() throws InterruptedException  {
        Flux<Integer> oneToFive = Flux.range(1, 5).delayElements(Duration.ofMillis(500));
        Flux<Integer> sixToTen = Flux.range(6, 5).delayElements(Duration.ofMillis(1000));

        /*
        What's happening here is is that ZIP is returning a Tuple. A Tuple is a container that pairs
        up elements.  The second argument to .zip() is an anonymous function that takes two arguments
        and returns something.  In this case it's returning a string that's composed of the two elements and some spacing and a comma.  This is passed on to the Subscribe method and is printed.

        It's important to note that the zip() will wait for all the elements to resolve before the anonymous function is executed.  It will only take as much time as it's needed to get the slowest Flux (or Mono) to resolve
         */
        Flux.zip(oneToFive, sixToTen,
                (item1, item2) -> item1 + ", " + + item2)
                .subscribe(System.out::println);

        Flux.zip(oneToFive, sixToTen)
            .map(data -> data.getClass().toString())
            .subscribe(System.out::println);

//        oneToFive.zipWith(sixToTen)
//                .subscribe(System.out::println);

        /*
        I set the thread sleep here to make sure we get sixToTen, which is going to take a full 5 seconds, but oneToFive only takes 2.5 seconds.  If things were done synchronously, we would not see output because the test would finish execution before the Fluxes resolved.  This shows that the test still only takes 5 seconds, not 7.5 seconds.
         */
        Thread.sleep(6000);
    }
}
