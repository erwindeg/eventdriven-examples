package nl.trifork.coins.version.query;

import nl.trifork.coins.coreapi.FindVersionQuery;
import nl.trifork.coins.coreapi.VersionCreatedEvent;
import nl.trifork.coins.coreapi.VersionDto;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VersionProjection {

    private static final Logger LOGGER = LoggerFactory.getLogger(VersionProjection.class);

    @Autowired
    private VersionRepository versionRepository;

    @Autowired
    private QueryUpdateEmitter queryUpdateEmitter;

    @EventHandler
    public void updateVersion(VersionCreatedEvent event) {
        LOGGER.info("Handling event");
        this.versionRepository.save(new VersionEntity(event.getId()));
        this.queryUpdateEmitter.emit(FindVersionQuery.class, query -> event.getId().equals(query.getId()), new VersionDto(event.getId()));
    }


    @QueryHandler
    public VersionDto getVersion(FindVersionQuery findVersionQuery) {
        VersionEntity versionEntity = this.versionRepository.getOne(findVersionQuery.getId());
        if(versionEntity == null){
            return null;
        }
        return new VersionDto(versionEntity.getId());
    }
}
