package sk.stuba.sdg.isbe.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sk.stuba.sdg.isbe.domain.model.Command;
import sk.stuba.sdg.isbe.services.CommandService;

@RestController
@RequestMapping("api/jobs/command")
public class CommandController {

    @Autowired
    private CommandService commandService;

    @PostMapping("create/{command}")
    public Command createCommand(@PathVariable Command command) {
        return commandService.createCommand(command);
    }

    @GetMapping("getCommandById/{commandId}")
    public Command getCommand(@PathVariable String commandId) {
        return commandService.getCommandById(commandId);
    }

    @GetMapping("getCommandByName/{commandName}")
    public Command getCommandByName(@PathVariable String commandName) {
        return commandService.getCommandByName(commandName);
    }

    @DeleteMapping("delete/{commandId}")
    public Command deleteCommand(@PathVariable String commandId) {
        return commandService.deleteCommand(commandId);
    }

    @PutMapping("update/{commandId}/{updateCommand}")
    public Command updateCommand(@PathVariable String commandId, @PathVariable Command updateCommand) {
        return commandService.updateCommand(commandId, updateCommand);
    }
}
