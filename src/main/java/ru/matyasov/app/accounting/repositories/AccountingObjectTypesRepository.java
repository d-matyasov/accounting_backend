package ru.matyasov.app.accounting.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.matyasov.app.accounting.models.references.system.AccountingObjectType;

@Repository
public interface AccountingObjectTypesRepository extends JpaRepository<AccountingObjectType, Integer> {
}
