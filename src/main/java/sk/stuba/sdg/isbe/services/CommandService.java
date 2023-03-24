package sk.stuba.sdg.isbe.services;

import sk.stuba.sdg.isbe.domain.model.Command;

public interface CommandService {

    Command createCommand(Command command);

    Command getCommandById(String commandId, boolean deactivated);

    Command getCommandByName(String name);

    Command deleteCommand(String commandId);
}
