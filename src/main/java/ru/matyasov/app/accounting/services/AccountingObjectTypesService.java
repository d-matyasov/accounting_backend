package ru.matyasov.app.accounting.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.matyasov.app.accounting.models.references.system.AccountingObjectType;
import ru.matyasov.app.accounting.repositories.AccountingObjectTypesRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class AccountingObjectTypesService {

    private final AccountingObjectTypesRepository accountingObjectTypesRepository;

    @Autowired
    public AccountingObjectTypesService(AccountingObjectTypesRepository accountingObjectTypesRepository) {
        this.accountingObjectTypesRepository = accountingObjectTypesRepository;
    }

    public List<AccountingObjectType> getAll() {

        return accountingObjectTypesRepository.findAll();

    }

    public Optional<AccountingObjectType> getById(int id) {

        return accountingObjectTypesRepository.findById(id);

    }

    @Transactional
    public AccountingObjectType create(AccountingObjectType accountingObjectType) {

        accountingObjectTypesRepository.save(accountingObjectType);

        return accountingObjectType;

    }

    @Transactional
    public AccountingObjectType update(int id, AccountingObjectType updatedAccountingObjectType) {

        updatedAccountingObjectType.setId(id);

        accountingObjectTypesRepository.save(updatedAccountingObjectType);

        return updatedAccountingObjectType;

    }

    @Transactional
    public void delete(int id) {

        accountingObjectTypesRepository.deleteById(id);

    }
}
