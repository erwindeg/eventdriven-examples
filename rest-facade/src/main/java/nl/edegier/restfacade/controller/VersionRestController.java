package nl.edegier.restfacade.controller;


import nl.edegier.coreapi.CreateVersionCommand;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class VersionRestController {

    private final CommandGateway commandGateway;

    public VersionRestController(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @RequestMapping(value = "/version",
            produces = {"application/json;charset=UTF-8"},
            method = RequestMethod.GET)
    public String getVersion() {
        this.commandGateway.send(new CreateVersionCommand(UUID.randomUUID().toString()));

        return "initial version";
    }
}
