package ru.matyasov.app.accounting.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.matyasov.app.accounting.models.references.user.AccountingObject;
import ru.matyasov.app.accounting.repositories.AccountingObjectsRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class AccountingObjectsService {

    private final AccountingObjectsRepository accountingObjectsRepository;

    @Autowired
    public AccountingObjectsService(AccountingObjectsRepository accountingObjectsRepository) {
        this.accountingObjectsRepository = accountingObjectsRepository;
    }

    public List<AccountingObject> getAll() {

        return accountingObjectsRepository.findAll();

    }

    public Optional<AccountingObject> getById(int id) {

        return accountingObjectsRepository.findById(id);

    }

    @Transactional
    public AccountingObject create(AccountingObject accountingObject) {

        accountingObjectsRepository.save(accountingObject);

        return accountingObject;

    }

    @Transactional
    public AccountingObject update(int id, AccountingObject updatedAccountingObject) {

        updatedAccountingObject.setId(id);

        accountingObjectsRepository.save(updatedAccountingObject);

        return updatedAccountingObject;

    }

    @Transactional
    public void delete(int id) {

        accountingObjectsRepository.deleteById(id);

    }
}
