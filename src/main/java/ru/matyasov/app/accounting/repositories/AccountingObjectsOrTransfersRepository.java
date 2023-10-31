package ru.matyasov.app.accounting.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.matyasov.app.accounting.models.views.AccountingObjectOrTransfer;

@Repository
public interface AccountingObjectsOrTransfersRepository extends JpaRepository<AccountingObjectOrTransfer, String> {
}
