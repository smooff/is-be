package sk.stuba.sdg.isbe.controllers;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sk.stuba.sdg.isbe.domain.model.User;
import sk.stuba.sdg.isbe.services.UserService;

import java.util.List;

@RestController
@RequestMapping("api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public List<User> getUsers() {return userService.getUsers();}

    @Operation(summary = "Create new user")
    @PostMapping("/create")
    public User createUser(@Valid @RequestBody User user) {
        return userService.createUser(user);
    }

    @Operation(summary = "Get user by id")
    @GetMapping("/getUserById/{userId}")
    public User getUserById(@PathVariable String userId) {
        return userService.getUserById(userId);
    }

    @Operation(summary = "Login user by name and password")
    @PostMapping("/loginUser/{name}/{password}")
    public User loginUser(@PathVariable String name, @PathVariable String password){
        return userService.loginUser(name, password);
    }

    @Operation(summary = "Login user by email")
    @PostMapping("/googleLoginUser/{mail}")
    public User googleLoginUser(@PathVariable String mail){
        return userService.googleLoginUser(mail);
    }

    @Operation(summary = "Update user through json")
    @PostMapping("/updateUser/{userId}")
    public User updateUser(@PathVariable String userId, @Valid @RequestBody User changeUser){
        return userService.updateUser(userId, changeUser);
    }

    @Operation(summary = "Delete user")
    @DeleteMapping("deleteUser/{userId}")
    public User deleteUser(@PathVariable String userId){
        return userService.deleteUser(userId);
    }
}
