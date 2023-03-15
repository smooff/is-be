package sk.stuba.sdg.isbe.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    UserRepository userRepository;

    @GetMapping("/getUsers")
    public List<User> getUser() {
        return userRepository.findAll();
    }

    @GetMapping("/addUser")
    public void addUser(@RequestBody User user) {
        userRepository.save(user);
    }
}
