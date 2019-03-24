package nl.trifork.coins;

import org.assertj.core.util.Arrays;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import static java.time.Duration.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
    public void subscribeShouldCallConsumer1time(){
        Mono<Integer> intMono = Mono.just(1);
        intMono.subscribe(consumer);

        verify(consumer, times(1)).accept(any(Integer.class));
    }


    /*
     * Flux subscribe()
     * */
    @Test
    public void subscribeShouldCallConsumer10times() {
        Flux<Integer> rangeFlux = Flux.range(0,10);
        rangeFlux
                .doOnEach(value -> System.out.println(value))
                .subscribe(consumer);

        verify(consumer, times(10)).accept(any(Integer.class));
    }

    /*
     * Mono map()
     * */
    @Test
    public void mapShouldConvertIntToString() {
        Mono<Integer> intMono = Mono.just(1);
        intMono
                .map(item -> "Number: "+item)
                .subscribe(consumer);

        verify(consumer, times(1)).accept(any(String.class));
    }


    /*
     * Flux map()
     * */
    @Test
    public void mapShouldConvertIntsToStrings() {
        Flux<Integer> rangeFlux = Flux.range(0,10);
        rangeFlux
                .map(item -> "Number: "+item)
                .doOnEach(value -> System.out.println(value))
                .subscribe(consumer);

        verify(consumer, times(10)).accept(any(String.class));
    }

    /*
     * Flux map() with method reference
     * */
    @Test
    public void mapShouldCalculateValues() {
        Flux<Integer> rangeFlux = Flux.range(0,10);
        rangeFlux
                .map(this::someSuperFastCalculation)
                .doOnEach(value -> System.out.println(value))
                .subscribe(consumer);

        verify(consumer, times(10)).accept(any(Integer.class));
    }

    /*
     * Flux flatMap()
     * */
    @Test
    public void flatMapShouldCalculateValuesAsync() {
        Flux<Integer> rangeFlux = Flux.range(0,10);
        rangeFlux
                .flatMap(this::someSuperLongCalculation)
                .doOnEach(value -> System.out.println(value))
                .subscribe(consumer);

        verify(consumer, times(10)).accept(any(Integer.class));
    }


    /*
     * Flux filter()
     * */
    @Test
    public void filterShouldOnlyPrintEventNumbers() {
        Flux<Integer> rangeFlux = Flux.range(0,10);
        rangeFlux
                .filter(item -> item % 2 ==0)
                .doOnEach(value -> System.out.println(value))
                .subscribe(consumer);

        verify(consumer, times(5)).accept(any(Integer.class));
    }

    /*
     * Flux timeout()
     * */
    @Test
    public void timeoutShouldThrowException(){
        Flux neverFlux = Flux.never();
        neverFlux
                .timeout(ofSeconds(1))
                .subscribe(consumer,errorConsumer);

        verify(consumer, times(0)).accept(any(String.class));
        verify(errorConsumer, timeout(1100)).accept(any(Exception.class));
    }

    /*
     * Flux onErrorReturn
     * */
    @Test
    public void timeoutShouldReturnDefaultValue(){
        Flux neverFlux = Flux.never();
        neverFlux
                .timeout(ofSeconds(1))
                .doOnEach(value -> System.out.println(value))
                .onErrorReturn("There was a timeout")
                .doOnEach(value -> System.out.println(value))
                .subscribe(consumer,errorConsumer);

        verify(consumer, timeout(1200)).accept(any(String.class));
        verify(errorConsumer, times(0)).accept(any(Exception.class));
    }


    @Test
    public void combinedOperatorsShouldMatchSuccess(){
        Flux.just("STARTED","PENDING","SUCCESS")
                .doOnEach(value -> System.out.println(value))
                .filter(value -> "SUCCESS".equals(value))
                .timeout(ofSeconds(1))
                .next()
                .onErrorReturn("ERROR")
                .subscribe(consumer);
        verify(consumer, timeout(1500)).accept(eq("SUCCESS"));
    }

    @Test
    public void combinedOperatorsShouldMatchErrorForException(){
        Flux.<String>generate(sink -> sink.next("STARTED")).take(5).delayElements(ofMillis(500))
                .doOnEach(value -> System.out.println(value))
                .filter(value -> "SUCCESS".equals(value))
                .timeout(ofSeconds(1))
                .next()
                .onErrorReturn("ERROR")
                .subscribe(consumer);
        verify(consumer, timeout(1500)).accept(eq("ERROR"));
    }

    private int someSuperFastCalculation(Integer value) {
        return value*2;
    }

    private Flux<Integer> someSuperLongCalculation(Integer value){
        return Flux.just(value*2);
    }
}
