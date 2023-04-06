package sk.stuba.sdg.isbe.services;

import sk.stuba.sdg.isbe.domain.model.Command;

import java.util.List;

public interface CommandService {

    Command createCommand(Command command);

    List<Command> getAllCommands();

    Command getCommandById(String commandId);

    Command getCommandByName(String name);

    Command deleteCommand(String commandId);

    Command updateCommand(String commandId, Command updateCommand);

    List<Command> getCommandsByDeviceType(String deviceType);
}
