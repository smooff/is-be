package sk.stuba.sdg.isbe.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sk.stuba.sdg.isbe.repositories.CommandRepository;
import sk.stuba.sdg.isbe.services.CommandService;

@Service
public class CommandServiceImpl implements CommandService {

    @Autowired
    private CommandRepository commandRepository;
}
