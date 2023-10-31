package ru.matyasov.app.accounting.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.matyasov.app.accounting.models.references.system.User;
import ru.matyasov.app.accounting.repositories.UsersRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UsersService {

    private final UsersRepository usersRepository;

    @Autowired
    public UsersService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    public List<User> getAll() {

        return usersRepository.findAll();

    }

    public Optional<User> getById(int id) {

        return usersRepository.findById(id);

    }

    @Transactional
    public User create(User accountingObject) {

        usersRepository.save(accountingObject);

        return accountingObject;

    }

    @Transactional
    public User update(int id, User updatedUser) {

        updatedUser.setId(id);

        usersRepository.save(updatedUser);

        return updatedUser;

    }

    @Transactional
    public void delete(int id) {

        usersRepository.deleteById(id);

    }
}
