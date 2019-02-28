package nl.edegier.version;

import nl.edegier.coreapi.FindUsersQuery;
import org.axonframework.queryhandling.QueryHandler;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.List;

import static java.util.Arrays.asList;

@Service
public class UserService {


    @Autowired
    private QueryUpdateEmitter queryUpdateEmitter;


    @QueryHandler
    public List query(FindUsersQuery findUsersQuery) {
        WebClient.create("https://reqres.in/api/users?page=1").get()
                .exchange()
                .flatMap(response -> response.bodyToMono(HashMap.class).map(map -> map.get("data")).map(users -> (List) users))
                .subscribe(users -> this.queryUpdateEmitter.emit(FindUsersQuery.class, query -> true, users));
        return asList(new HashMap());
    }
}
