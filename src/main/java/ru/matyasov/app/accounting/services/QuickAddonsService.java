package ru.matyasov.app.accounting.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.matyasov.app.accounting.models.references.user.QuickAddon;
import ru.matyasov.app.accounting.repositories.QuickAddonsRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class QuickAddonsService {

    private final QuickAddonsRepository QuickAddonsRepository;

    @Autowired
    public QuickAddonsService(QuickAddonsRepository QuickAddonsRepository) {
        this.QuickAddonsRepository = QuickAddonsRepository;
    }

    public List<QuickAddon> getAll() {

        return QuickAddonsRepository.findAll();

    }

    public Optional<QuickAddon> getById(int id) {

        return QuickAddonsRepository.findById(id);

    }

    @Transactional
    public QuickAddon create(QuickAddon quickAddon) {

        QuickAddonsRepository.save(quickAddon);

        return quickAddon;

    }

    @Transactional
    public QuickAddon update(int id, QuickAddon updatedQuickAddon) {

        updatedQuickAddon.setId(id);

        QuickAddonsRepository.save(updatedQuickAddon);

        return updatedQuickAddon;

    }

    @Transactional
    public void delete(int id) {

        QuickAddonsRepository.deleteById(id);

    }
}
