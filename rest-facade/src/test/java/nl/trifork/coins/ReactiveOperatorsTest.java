package nl.trifork.coins;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;

import static java.time.Duration.ofMillis;
import static java.time.Duration.ofSeconds;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ReactiveOperatorsTest {

    @Mock
    Consumer consumer;

    @Mock
    Consumer errorConsumer;

    /*
     * Mono subscribe()
     * */
    @Test
    public void subscribeShouldCallConsumer1time() {
        Mono<Integer> intMono = Mono.just(1);
        //TODO: which operator?

        verify(consumer, times(1)).accept(any(Integer.class));
    }


    /*
     * Flux subscribe()
     * */
    @Test
    public void subscribeShouldCallConsumer10times() {
        Flux<Integer> rangeFlux = Flux.range(0, 10);
        //TODO: which operator?

        verify(consumer, times(10)).accept(any(Integer.class));
    }

    /*
     * Mono map()
     * */
    @Test
    public void mapShouldConvertIntToString() {
        Mono<Integer> intMono = Mono.just(1);
        //TODO: which operator? don't forget to subscribe!

        verify(consumer, times(1)).accept(any(String.class));
    }


    /*
     * Flux map()
     * */
    @Test
    public void mapShouldConvertIntsToStrings() {
        Flux<Integer> rangeFlux = Flux.range(0, 10);
        //TODO: which operator? don't forget to subscribe!

        verify(consumer, times(10)).accept(any(String.class));
    }

    /*
     * Flux map() with method reference
     * */
    @Test
    public void mapShouldCalculateValues() {
        Flux<Integer> rangeFlux = Flux.range(0, 10);
        //TODO: which operator, you can use this::someSuperFastCalculation. Don't forget to subscribe!

        verify(consumer, times(10)).accept(any(Integer.class));
    }

    /*
     * Flux flatMap()
     * */
    @Test
    public void flatMapShouldCalculateValuesAsync() {
        Flux<Integer> rangeFlux = Flux.range(0, 10);
        //TODO: which operator, you can use this::someSuperLongCalculation. Don't forget to subscribe!

        verify(consumer, times(10)).accept(any(Integer.class));
    }


    /*
     * Flux filter()
     * */
    @Test
    public void filterShouldOnlyPrintEventNumbers() {
        Flux<Integer> rangeFlux = Flux.range(0, 10);
        //TODO: which operator? don't forget to subscribe!

        verify(consumer, times(5)).accept(any(Integer.class));
    }

    /*
     * Flux timeout()
     * */
    @Test
    public void timeoutShouldThrowException() {
        Flux neverFlux = Flux.never();
        //TODO: which operator? don't forget to subscribe!

        verify(consumer, times(0)).accept(any(String.class));
        verify(errorConsumer, timeout(1100)).accept(any(Exception.class));
    }

    /*
     * Flux onErrorReturn
     * */
    @Test
    public void timeoutShouldReturnDefaultValue() {
        Flux neverFlux = Flux.never();
//        neverFlux.timeout(ofSeconds(1)) //uncomment
        //TODO: which operator? don't forget to subscribe!

        verify(consumer, timeout(1100)).accept(any(String.class));
        verify(errorConsumer, times(0)).accept(any(Exception.class));
    }


    @Test
    public void combinedOperatorsShouldMatchSuccess() {
        Flux.just("STARTED", "PENDING", "SUCCESS")
                .doOnEach(value -> System.out.println(value))
                .filter(value -> "SUCCESS".equals(value))
                .timeout(ofSeconds(1))
                .next()
                .onErrorReturn("ERROR")
                .subscribe(consumer);
        verify(consumer, timeout(1200)).accept(eq("SUCCESS"));
    }

    @Test
    public void combinedOperatorsShouldMatchErrorForException() {
        Flux.<String>generate(sink -> sink.next("STARTED")).take(5).delayElements(ofMillis(500))
                .doOnEach(value -> System.out.println(value))
                .filter(value -> "SUCCESS".equals(value))
                .timeout(ofSeconds(1))
                .next()
                .onErrorReturn("ERROR")
                .subscribe(consumer);
        verify(consumer, timeout(1000)).accept(eq("ERROR"));
    }

    private int someSuperFastCalculation(Integer value) {
        return value * 2;
    }

    private Flux<Integer> someSuperLongCalculation(Integer value) {
        return Flux.just(value * 2);
    }
}
