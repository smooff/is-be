package sk.stuba.sdg.isbe.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import sk.stuba.sdg.isbe.domain.model.Command;
import sk.stuba.sdg.isbe.handlers.exceptions.EntityExistsException;
import sk.stuba.sdg.isbe.handlers.exceptions.InvalidEntityException;
import sk.stuba.sdg.isbe.handlers.exceptions.NotFoundCustomException;
import sk.stuba.sdg.isbe.repositories.CommandRepository;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class CommandServiceTests {
    @Autowired
    private CommandService commandService;
    @Autowired
    private CommandRepository commandRepository;

    @Test
    void testCreateCommand() {
        Command command = new Command();
        Exception exception = assertThrows(InvalidEntityException.class, () -> {
            commandService.createCommand(command);
        });
        String expected = "Name of the command is not valid!";
        assertEquals(expected, exception.getMessage());

        command.setName("command" + Instant.now().toEpochMilli());
        command.setParams(List.of(1,2,3));
        commandService.createCommand(command);
        exception = assertThrows(EntityExistsException.class, () -> {
            commandService.createCommand(command);
        });
        expected = "Command with name: '" + command.getName() + "' already exists!";
        assertEquals(expected, exception.getMessage());

        command.setParams(null);
        command.setName(command.getName() + "1");
        exception = assertThrows(InvalidEntityException.class, () -> {
            commandService.createCommand(command);
        });
        expected = "Command does not contain any parameters!";
        assertEquals(expected, exception.getMessage());

        commandRepository.delete(command);
    }

    @Test
    void testDeleteCommand() {
        Command command = new Command();
        command.setName("command " + Instant.now().toEpochMilli());
        command.setParams(List.of(1,2,3));
        commandService.createCommand(command);
        commandService.deleteCommand(command.getId());
        Exception exception = assertThrows(NotFoundCustomException.class, () -> {
            commandService.deleteCommand(command.getId());
        });
        String expected = "Command with ID: '" + command.getId() + "' was not found!";
        assertEquals(expected, exception.getMessage());

        commandRepository.delete(command);
    }

    @Test
    void testGetCommand() {
        Command command = new Command();
        command.setName("command " + Instant.now().toEpochMilli());
        command.setParams(List.of(1,2,3));
        command.setDeactivated(true);
        commandService.createCommand(command);
        Exception exception = assertThrows(NotFoundCustomException.class, () -> {
            commandService.getCommandById(command.getId());
        });
        String expected = "Command with ID: '" + command.getId() + "' was not found!";
        assertEquals(expected, exception.getMessage());

        command.setDeactivated(false);
        commandService.createCommand(command);
        commandService.getCommandById(command.getId());
        commandRepository.delete(command);
    }

}
