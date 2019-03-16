package nl.trifork.coins;

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

    //Mono, Flux, map, subscribe, flatMap, timout, onErrorReturn, filter

    @Mock
    Consumer consumer;

    @Mock
    Consumer errorConsumer;

    @Test
    public void subscribeShouldCallConsumer1time(){
        Mono<Integer> intMono = Mono.just(1);
        intMono.subscribe(consumer);

        verify(consumer, times(1)).accept(any(Integer.class));
    }

    @Test
    public void mapShouldConvertIntToString() {
        Mono<Integer> intMono = Mono.just(1);
        intMono
                .map(item -> "Number: "+item)
                .subscribe(consumer);

        verify(consumer, times(1)).accept(any(String.class));
    }

    @Test
    public void subscribeShouldCallConsumer10times() {
        Flux<Integer> rangeFlux = Flux.range(0,10);
        rangeFlux.subscribe(consumer);

        verify(consumer, times(10)).accept(any(Integer.class));
    }

    @Test
    public void mapShouldConvertIntsToStrings() {
        Flux<Integer> rangeFlux = Flux.range(0,10);
        rangeFlux
                .map(item -> "Number: "+item)
                .subscribe(consumer);

        verify(consumer, times(10)).accept(any(String.class));
    }

    @Test(expected = Exception.class)
    public void timoutShouldThrowException(){
        Flux neverFlux = Flux.never();
        neverFlux.timeout(ofSeconds(1)).blockLast();
    }
}
