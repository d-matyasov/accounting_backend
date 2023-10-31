package ru.matyasov.app.accounting.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.matyasov.app.accounting.models.Operation;

@Repository
public interface OperationsRepository extends JpaRepository<Operation, Integer> {


}
