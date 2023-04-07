package sk.stuba.sdg.isbe.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sk.stuba.sdg.isbe.domain.model.User;
import sk.stuba.sdg.isbe.handlers.exceptions.EntityExistsException;
import sk.stuba.sdg.isbe.handlers.exceptions.InvalidEntityException;
import sk.stuba.sdg.isbe.repositories.UserRepository;
import sk.stuba.sdg.isbe.services.UserService;

import java.time.Instant;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User createUser(User user){
        validateUser(user);

        user.setCreatedAt(Instant.now().toEpochMilli());
        return userRepository.save(user);
    }

    @Override
    public List<User> getUsers(){
        return userRepository.findAll();
    }

    @Override
    public User getUserById(String userId){
        if (userId == null || userId.isEmpty()) {
            throw new InvalidEntityException("User id is not set!");
        }

        return userRepository.getUserByUid(userId);
    }

    @Override
    public User loginUser(String name, String password){
        User user = userRepository.getUserByNameAndPassword(name, password);
        validateUser(user);
        if (user.getName() == null || user.getName().isEmpty() || user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new EntityExistsException("User name or password wrong!");
        }

        return user;
    }

    @Override
    public User googleLoginUser(String mail){
        User user = userRepository.getUserByMail(mail);
        validateUser(user);
        if (user.getMail() == null || user.getMail().isEmpty()) {
            throw new EntityExistsException("User mail wrong!");
        }

        return user;
    }

    @Override
    public User updateUser(String userId, User changeUser){
        User user = getUserById(userId);

        if (userId == null || userId.isEmpty()) {
            throw new InvalidEntityException("userId with changes is null!");
        }

        if (changeUser.getName() != null) {
            user.setName(changeUser.getName());
        }
        if (changeUser.getMail() != null) {
            user.setMail(changeUser.getMail());
        }
        if (changeUser.getPassword() != null) {
            user.setPassword(changeUser.getPassword());
        }
        if (changeUser.getPermissions() != null) {
            user.setPermissions(changeUser.getPermissions());
        }

        return userRepository.save(user);
    }

    @Override
    public User deleteUser(String userId){
        User user = getUserById(userId);
        user.setDeactivated(true);
        return userRepository.save(user);
    }

    @Override
    public void validateUser(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            throw new InvalidEntityException("User name is not set!");
        }
        if (user.getMail() == null || user.getMail().isEmpty()) {
            throw new InvalidEntityException("User mail is not set!");
        }
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new InvalidEntityException("User password is not set!");
        }
//        if (user.getPermissions() == null) {
//            throw new InvalidEntityException("User permissions is not set!");
//        }
    }
}
