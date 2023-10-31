package ru.matyasov.app.accounting.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.matyasov.app.accounting.models.references.system.Currency;
import ru.matyasov.app.accounting.repositories.CurrenciesRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class CurrenciesService {

    private final CurrenciesRepository currenciesRepository;

    @Autowired
    public CurrenciesService(CurrenciesRepository currenciesRepository) {
        this.currenciesRepository = currenciesRepository;
    }

    public List<Currency> getAll() {
        return currenciesRepository.findAll();
    }

    public Optional<Currency> getById(int id) {

        return currenciesRepository.findById(id);
    }

    @Transactional
    public Currency create(Currency currency) {

        currenciesRepository.save(currency);

        return currency;

    }

    @Transactional
    public Currency update(int id, Currency updatedCurrency) {

        updatedCurrency.setId(id);

        currenciesRepository.save(updatedCurrency);

        return updatedCurrency;

    }

    @Transactional
    public void delete(int id) {

        currenciesRepository.deleteById(id);

    }

}
