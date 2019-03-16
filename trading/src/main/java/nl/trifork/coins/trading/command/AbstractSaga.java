package nl.trifork.coins.trading.command;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AbstractSaga {
    @Autowired
    protected transient CommandGateway commandGateway;

    public CommandGateway getCommandGateway() {
        return commandGateway;
    }

    @Autowired
    public void setCommandGateway(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }


}
