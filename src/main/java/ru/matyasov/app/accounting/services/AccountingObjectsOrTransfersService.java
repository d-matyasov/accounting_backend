package ru.matyasov.app.accounting.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.matyasov.app.accounting.models.views.AccountingObjectOrTransfer;
import ru.matyasov.app.accounting.repositories.AccountingObjectsOrTransfersRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class AccountingObjectsOrTransfersService {

    private final AccountingObjectsOrTransfersRepository accountingObjectsOrTransfersRepository;

    @Autowired
    public AccountingObjectsOrTransfersService(AccountingObjectsOrTransfersRepository accountingObjectsOrTransfersRepository) {
        this.accountingObjectsOrTransfersRepository = accountingObjectsOrTransfersRepository;
    }

    public List<AccountingObjectOrTransfer> getAll() {

        return accountingObjectsOrTransfersRepository.findAll();

    }

    public Optional<AccountingObjectOrTransfer> getById(String id) {
        return accountingObjectsOrTransfersRepository.findById(id);
    }

}
