package ru.matyasov.app.accounting.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.matyasov.app.accounting.models.references.user.OperationCategory;
import ru.matyasov.app.accounting.models.references.user.QuickAddon;
import ru.matyasov.app.accounting.repositories.OperationCategoriesRepository;
import ru.matyasov.app.accounting.utils.Common;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class OperationCategoriesService {

    private final OperationCategoriesRepository operationCategoriesRepository;

    private final Common common;

    @Autowired
    public OperationCategoriesService(OperationCategoriesRepository operationCategoriesRepository, Common common) {
        this.operationCategoriesRepository = operationCategoriesRepository;
        this.common = common;
    }

    public List<OperationCategory> getAll() {

        return operationCategoriesRepository.findAll();

    }

    public Optional<OperationCategory> getById(int id) {

        return operationCategoriesRepository.findById(id);

    }

    @Transactional
    public OperationCategory create(OperationCategory operationCategory) {

        operationCategoriesRepository.save(operationCategory);

        return operationCategory;

    }

    @Transactional
    public OperationCategory update(int id, OperationCategory updatedOperationCategory) {

        updatedOperationCategory.setId(id);

        updatedOperationCategory.setQuickAddonList(operationCategoriesRepository.findById(id).orElse(null).getQuickAddonList());

        operationCategoriesRepository.save(updatedOperationCategory);

        return updatedOperationCategory;

    }

    @Transactional
    public void delete(int id) {

        operationCategoriesRepository.deleteById(id);

    }

    public List<QuickAddon> getQuickAddonList(int id) {

        return operationCategoriesRepository.findById(id).orElse(null).getQuickAddonList();

    }

    @Transactional
    public List<QuickAddon> addQuickAddonList(OperationCategory operationCategory, List<QuickAddon> addedQuickAddonList) {

        operationCategory.getQuickAddonList().addAll(addedQuickAddonList);

        operationCategoriesRepository.save(operationCategory);

        return operationCategory.getQuickAddonList();

    }

    @Transactional
    public List<QuickAddon> deleteQuickAddonList(OperationCategory operationCategory, String deletedQuickAddonIds) {

        List<QuickAddon> deletedQuickAddonList = operationCategory.getQuickAddonList().stream().filter(e -> common.stringToIntegerList(deletedQuickAddonIds).contains(e.getId())).toList();

        operationCategory.getQuickAddonList().removeAll(deletedQuickAddonList);

        operationCategoriesRepository.save(operationCategory);

        return operationCategory.getQuickAddonList();

    }
}
