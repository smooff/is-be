package sk.stuba.sdg.isbe.controllers;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sk.stuba.sdg.isbe.domain.model.Command;
import sk.stuba.sdg.isbe.services.CommandService;

@RestController
@RequestMapping("api/jobs/command")
public class CommandController {

    @Autowired
    private CommandService commandService;

    @Operation(summary = "Create a command in database using object")
    @PostMapping("create")
    public Command createCommand(@Valid @RequestBody Command command) {
        return commandService.createCommand(command);
    }

    @Operation(summary = "Get command by ID")
    @GetMapping("getCommandById/{commandId}")
    public Command getCommand(@PathVariable String commandId) {
        return commandService.getCommandById(commandId);
    }

    @Operation(summary = "Get command by name")
    @GetMapping("getCommandByName/{commandName}")
    public Command getCommandByName(@PathVariable String commandName) {
        return commandService.getCommandByName(commandName);
    }

    @Operation(summary = "Delete command by ID")
    @DeleteMapping("delete/{commandId}")
    public Command deleteCommand(@PathVariable String commandId) {
        return commandService.deleteCommand(commandId);
    }

    @Operation(summary = "Update command by ID using command object")
    @PutMapping("update/{commandId}")
    public Command updateCommand(@PathVariable String commandId, @Valid @RequestBody Command updateCommand) {
        return commandService.updateCommand(commandId, updateCommand);
    }
}
