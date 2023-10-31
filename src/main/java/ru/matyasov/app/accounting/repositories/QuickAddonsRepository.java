package ru.matyasov.app.accounting.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.matyasov.app.accounting.models.references.user.QuickAddon;

@Repository
public interface QuickAddonsRepository extends JpaRepository<QuickAddon, Integer> {
}
