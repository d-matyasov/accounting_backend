package ru.matyasov.app.accounting.services;

import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.matyasov.app.accounting.models.Operation;
import ru.matyasov.app.accounting.models.references.user.AccountingObject;
import ru.matyasov.app.accounting.repositories.AccountingObjectsRepository;
import ru.matyasov.app.accounting.repositories.OperationsRepository;

import java.util.*;


@Service
@Transactional(readOnly = true)
public class OperationsService {

    @Autowired
    private final OperationsRepository operationsRepository;

    @Autowired
    private final AccountingObjectsRepository accountingObjectsRepository;

    @Autowired
    private final AccountingObjectsService accountingObjectsService;


    @Autowired
    private EntityManager entityManager;

    @Autowired
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public OperationsService(OperationsRepository operationsRepository, AccountingObjectsRepository accountingObjectsRepository, AccountingObjectsService accountingObjectsService, JdbcTemplate jdbcTemplate) {
        this.operationsRepository = operationsRepository;
        this.accountingObjectsRepository = accountingObjectsRepository;
        this.accountingObjectsService = accountingObjectsService;
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Operation> getAll() {

        return operationsRepository.findAll();

    }

    public Optional<Operation> getById(int id) {

        return operationsRepository.findById(id);

    }

    @Transactional
    public Operation create(Operation operation) {

        if (operation.getPairOperation() == null) {

            // Если индекс по дате не определён (меньше 1), то найдём максимальный по дате и добавим 1
            if (operation.getIndexByDate() < 1) {

                operation.setIndexByDate(getMaxIndexByDate(operation.getDate()) + 1);

            }

            // добавить "дырку" в индексах
            recalculateIndexesByDate(operation.getDate(), operation.getIndexByDate(), 1);

            operationsRepository.save(operation);

            // если факт, то учесть в текущей сумме на объекте учёта
            if (operation.getIsFact()) {

                AccountingObject accountingObject = operation.getAccountingObject();

                if (operation.getAmountPlus() != null) {
                    accountingObject.setCurrentAmount(accountingObject.getCurrentAmount() + operation.getAmountPlus());
                } else {
                    accountingObject.setCurrentAmount(accountingObject.getCurrentAmount() - operation.getAmountMinus());
                }

                accountingObjectsService.update(accountingObject.getId(), accountingObject);

            }

            // пересчитать суммы объектов учёта начиная с созданной операции
            recalculateAccountingObjectAmounts(operation.getAccountingObject(), operation.getDate(), operation.getIndexByDate());

        } else {

            Operation pairOperation;

            pairOperation = operation.getPairOperation();

            // Если индекс по дате не определён (меньше 1), то найдём максимальный по дате и добавим 1, к парной - добавим 2
            if (operation.getIndexByDate() < 1) {

                Integer maxIndexByDate = getMaxIndexByDate(operation.getDate());

                operation.setIndexByDate(maxIndexByDate + 1);

                pairOperation.setIndexByDate(maxIndexByDate + 2);
            }

            // добавить "дырку" в индексах
            recalculateIndexesByDate(operation.getDate(), operation.getIndexByDate(), 2);

            operation.setPairOperation(null);

            operationsRepository.save(operation);

            pairOperation.setPairOperation(operation);

            operationsRepository.save(pairOperation);

            operation.setPairOperation(pairOperation);

            operationsRepository.save(operation);

            // если факт, то учесть в текущих суммах на объектах учёта
            if (operation.getIsFact()) {

                AccountingObject accountingObject = operation.getAccountingObject();

                accountingObject.setCurrentAmount(accountingObject.getCurrentAmount() - operation.getAmountMinus());

                accountingObjectsService.update(accountingObject.getId(), accountingObject);

                AccountingObject pairAccountingObject = pairOperation.getAccountingObject();

                pairAccountingObject.setCurrentAmount(pairAccountingObject.getCurrentAmount() + pairOperation.getAmountPlus());

                accountingObjectsService.update(pairAccountingObject.getId(), pairAccountingObject);

            }

            // пересчитать суммы объектов учёта начиная с созданных операций
            recalculateAccountingObjectAmounts(operation.getAccountingObject(), operation.getDate(), operation.getIndexByDate());

            recalculateAccountingObjectAmounts(pairOperation.getAccountingObject(), pairOperation.getDate(), pairOperation.getIndexByDate());

        }

        return operation;

    }

    @Transactional
    public Operation update(int id, Operation updatedOperation) {

// Включить в случае реализации сложного алгоритма
//        Operation savedOperation = getById(id).orElse(null);
//
//        if (new LocalDate(updatedOperation.getDate()).compareTo(new LocalDate(savedOperation.getDate())) == 0
//                && updatedOperation.getAccountingObject() == savedOperation.getAccountingObject()
//                && updatedOperation.getPairOperation().getAccountingObject() == savedOperation.getPairOperation().getAccountingObject()
//                && updatedOperation.getIsFact() == savedOperation.getIsFact()
//                && ObjectUtils.compare(updatedOperation.getAmountPlus(), savedOperation.getAmountPlus()) == 0
//                && ObjectUtils.compare(updatedOperation.getAmountMinus(), savedOperation.getAmountMinus()) == 0
//                && ObjectUtils.compare(updatedOperation.getPairOperation().getAmountPlus(), savedOperation.getPairOperation().getAmountPlus()) == 0
//                && ObjectUtils.compare(updatedOperation.getCategory().getId(), savedOperation.getCategory().getId()) == 0
//                && ObjectUtils.compare(updatedOperation.getCategoryAddon(), savedOperation.getCategoryAddon()) == 0
//        ) {
//            throw new RuntimeException("Дата, объекты учёта, признак факта, сумма, категория и категория (доп.) не изменились. Сохранение изменений других параметров не реализовано.");
//        }

        updatedOperation.setId(id);

        Integer pairOperationId = updatedOperation.getPairOperation() == null ? null : updatedOperation.getPairOperation().getId();

        delete(updatedOperation.getId());

        updatedOperation.setId(null);

        if (updatedOperation.getPairOperation() != null) {

            updatedOperation.getPairOperation().setId(null);

        }

        Operation createdOperation = create(updatedOperation);

        entityManager.flush();

        changeIds(createdOperation.getId(), id, pairOperationId);

        createdOperation = operationsRepository.findById(id).orElse(null);

        return createdOperation;

    }

    @Transactional
    public void delete(int id) {

        Operation savedOperation = getById(id).orElse(null);

        Operation savedPairOperation = savedOperation.getPairOperation() == null ? null : getById(savedOperation.getPairOperation().getId()).orElse(null);

        if (savedPairOperation == null) {

            if (savedOperation.getIsFact()) {

                AccountingObject accountingObject = savedOperation.getAccountingObject();

                long amount = savedOperation.getAmountPlus() == null ? savedOperation.getAmountMinus() : - savedOperation.getAmountPlus();

                accountingObject.setCurrentAmount(accountingObject.getCurrentAmount() + amount);

                accountingObjectsRepository.save(accountingObject);

            }

            operationsRepository.deleteById(id);

            entityManager.flush();

            recalculateAccountingObjectAmounts(savedOperation.getAccountingObject(), savedOperation.getDate(), savedOperation.getIndexByDate());

        } else {

            if (savedOperation.getIsFact()) {

                AccountingObject accountingObject = savedOperation.getAccountingObject();

                accountingObject.setCurrentAmount(accountingObject.getCurrentAmount() + savedOperation.getAmountMinus());

                accountingObjectsRepository.save(accountingObject);

                AccountingObject pairAccountingObject = savedPairOperation.getAccountingObject();

                pairAccountingObject.setCurrentAmount(pairAccountingObject.getCurrentAmount() - savedPairOperation.getAmountPlus());

                accountingObjectsRepository.save(pairAccountingObject);

            }

            savedOperation.setPairOperation(null);

            operationsRepository.deleteById(id);

            operationsRepository.deleteById(savedPairOperation.getId());

            entityManager.flush();

            recalculateAccountingObjectAmounts(savedOperation.getAccountingObject(), savedOperation.getDate(), savedOperation.getIndexByDate());

            recalculateAccountingObjectAmounts(savedPairOperation.getAccountingObject(), savedPairOperation.getDate(), savedPairOperation.getIndexByDate());

        }

        // Пересчёт индексов по дате следующих операций


        recalculateIndexesByDate(savedOperation.getDate(), savedOperation.getIndexByDate());

    }

//    @Deprecated
//    @Transactional
//    public void shiftUp(int id) throws CloneNotSupportedException {
//
//        Operation movedOperation = operationsRepository.findById(id).orElse(null);
//
//        assert movedOperation != null;
//
//        Operation movedOperationClone = movedOperation.clone();
//
//        Operation previousOperation = operationsRepository.findById(getNearbyId(id, -1)).orElse(null);
//
//        Integer previousOperationId = previousOperation.getId();
//
//        Date previousDate = getNearbyDate(id, -1);
//
////        Если перед текущей операцией дата без операции:
////        установить в ней эту дату без операции и индекс 1, если есть пара у текущей операции, то в парной операции установить ту же дату и индекс 2
//
//        if ((previousOperation == null && previousDate != null) || previousOperation.getDate().before(previousDate)) {
//
//            movedOperation.setDate(previousDate);
//
//            movedOperation.setIndexByDate(1);
//
//            if (movedOperation.getPairOperation() != null) {
//
//                movedOperation.getPairOperation().setDate(previousDate);
//
//                movedOperation.getPairOperation().setIndexByDate(2);
//
//            }
//
//            operationsRepository.save(movedOperation);
//
//            entityManager.flush();
//
//            recalculateIndexesByDate(previousDate);
//
//            recalculateIndexesByDate(movedOperationClone.getDate());
//        }
//
////        Если есть предыдущая операция/пара операций:
////        создать дырку перед ней: date из неё/первой операции, indexByDate из неё/первой операции, size = <если у текущей операции нет парной 1, иначе 2>;
////        поставить текущую операцию/пару операций перед предыдущей: operation.date и operation.indexByDate те же, что для создания дырки, если есть парная операция, то pairOperation.date - то же, что для создания дырки, pairOperation.indexByDate = operation.indexByDate + 1;
////        пересчитать indexByDate по дате текущей операции: начать с indexByDate = <если дата предыдущей операции отличается от даты текущей 1, иначе: если текущая операция одиночная, то indexByDate из неё + 1, иначе indexByDate из парной операции + 1>;
////        если объект учёта из предыдущей операции/первой операции предыдущей пары совпадает с объектом учёта из текущей операции или её парной операции, то пересчитать суммы объекта учёта, начиная с date и indexByDate из предыдущей операции, заканчивая date и indexByDate из операции с совпадающим объектом учёта;
////        если объект учёта второй операции предыдущей пары совпадает с объектом учёта из текущей операции или её парной операции, то пересчитать суммы объекта учёта, начиная с date и indexByDate из второй операции предыдущей пары, заканчивая date и indexByDate из операции с совпадающим объектом учёта.
//
//
//        else if (previousOperation != null) {
//
////        создать дырку перед ней: date из неё/первой операции, indexByDate из неё/первой операции, size = <если у текущей операции нет парной 1, иначе 2>;
//            recalculateIndexesByDate(previousOperation.getDate(), previousOperation.getIndexByDate(), movedOperationClone.getPairOperation() == null ? 1 : 2);
//
////        поставить текущую операцию/пару операций перед предыдущей: operation.date и operation.indexByDate те же, что для создания дырки, если есть парная операция, то pairOperation.date - то же, что для создания дырки, pairOperation.indexByDate = operation.indexByDate + 1;
//            movedOperation.setDate(previousOperation.getDate());
//
//            movedOperation.setIndexByDate(previousOperation.getIndexByDate());
//
//            if (movedOperation.getPairOperation() != null) {
//
//                movedOperation.getPairOperation().setDate(movedOperation.getDate());
//
//                movedOperation.getPairOperation().setIndexByDate(movedOperation.getIndexByDate() + 1);
//
//            }
//
//            operationsRepository.save(movedOperation);
//
////        пересчитать indexByDate по дате текущей операции: начать с indexByDate = <если дата предыдущей операции отличается от даты текущей 1, иначе: если текущая операция одиночная, то indexByDate из неё + 1, иначе indexByDate из парной операции + 1>;
//
//            entityManager.flush();
//
//            recalculateIndexesByDate(movedOperationClone.getDate(), previousOperation.getDate() != movedOperationClone.getDate() ? 1 : movedOperationClone.getPairOperation() == null ? movedOperationClone.getIndexByDate() + 1 : movedOperationClone.getPairOperation().getIndexByDate() + 1, 0);
//
//
////            previousOperation = null;
////
////            System.out.println(previousOperation);
//
//            entityManager.detach(previousOperation);
//
//            if (previousOperation.getPairOperation() != null) {
//
//                entityManager.detach(previousOperation.getPairOperation());
//
//            }
//            previousOperation = operationsRepository.findById(previousOperationId).orElse(null);
//
//            System.out.println("movedOperation: " + movedOperation);
//            System.out.println("previousOperation: " + previousOperation);
//
////        если объект учёта из предыдущей операции/первой операции предыдущей пары совпадает с объектом учёта из текущей операции или её парной операции, то пересчитать суммы объекта учёта, начиная с date и indexByDate из предыдущей операции, заканчивая date и indexByDate из операции с совпадающим объектом учёта;
//            if (previousOperation.getAccountingObject() == movedOperation.getAccountingObject()) {
//
//                recalculateAccountingObjectAmounts(previousOperation.getAccountingObject(), movedOperation.getDate(), movedOperation.getIndexByDate(), previousOperation.getDate(), previousOperation.getIndexByDate());
//
//            }
//
//            if (movedOperation.getPairOperation() != null && previousOperation.getAccountingObject() == movedOperation.getPairOperation().getAccountingObject()) {
//
//                recalculateAccountingObjectAmounts(previousOperation.getAccountingObject(), movedOperation.getPairOperation().getDate(), movedOperation.getPairOperation().getIndexByDate(), previousOperation.getDate(), previousOperation.getIndexByDate());
//
//            }
//
////        если объект учёта второй операции предыдущей пары совпадает с объектом учёта из текущей операции или её парной операции, то пересчитать суммы объекта учёта, начиная с date и indexByDate из второй операции предыдущей пары, заканчивая date и indexByDate из операции с совпадающим объектом учёта.
//
//            if (previousOperation.getPairOperation() != null) {
//
//                if (previousOperation.getPairOperation().getAccountingObject() == movedOperation.getAccountingObject()) {
//
//                    recalculateAccountingObjectAmounts(previousOperation.getPairOperation().getAccountingObject(), movedOperation.getDate(), movedOperation.getIndexByDate(), previousOperation.getPairOperation().getDate(), previousOperation.getPairOperation().getIndexByDate());
//
//                }
//
//                if (movedOperation.getPairOperation() != null && previousOperation.getPairOperation().getAccountingObject() == movedOperation.getPairOperation().getAccountingObject()) {
//
//                    recalculateAccountingObjectAmounts(previousOperation.getPairOperation().getAccountingObject(), movedOperation.getPairOperation().getDate(), movedOperation.getPairOperation().getIndexByDate(), previousOperation.getPairOperation().getDate(), previousOperation.getPairOperation().getIndexByDate());
//
//                }
//
//            }
//
//
//        }
//    }

    @Transactional
    public void shiftUp(int id) {
        shift(id, -1);
    }

    @Transactional
    public void shiftDown(int id) {
        shift(id, 1);
    }

    @Transactional
    public void changeIsFact(int id, Boolean isFact) {

        Operation operation = getById(id).orElse(null);

        System.out.println(isFact != operation.getIsFact());

        System.out.println(isFact);

        System.out.println(operation);

        if (isFact != operation.getIsFact()) {

            AccountingObject accountingObject = operation.getAccountingObject();

            AccountingObject pairAccountingObject = operation.getPairOperation() != null ? operation.getPairOperation().getAccountingObject() : null;

            Long amount = operation.getAmountPlus() != null ? operation.getAmountPlus() : - operation.getAmountMinus();

            Long pairOperationAmount = pairAccountingObject != null ? operation.getPairOperation().getAmountPlus() : null;

            accountingObject.setCurrentAmount(accountingObject.getCurrentAmount() + (isFact ? amount : - amount));

            accountingObjectsRepository.save(accountingObject);

            operation.setIsFact(isFact);

            operationsRepository.save(operation);

            if (pairAccountingObject != null) {

                pairAccountingObject.setCurrentAmount(pairAccountingObject.getCurrentAmount() + (isFact ? pairOperationAmount : - pairOperationAmount));

                accountingObjectsRepository.save(pairAccountingObject);

                operation.getPairOperation().setIsFact(isFact);

                operationsRepository.save(operation.getPairOperation());

            }

        }

    }

    @Transactional
    public Object getOperationsTableJson(Date dateFrom, Date dateTo) {

        final SimpleJdbcCall getOperationsTableJson = new SimpleJdbcCall(jdbcTemplate).withFunctionName("get_operations_table_json");
        final Map<String, Object> params = new HashMap<>();
        params.put("p_date_from", new java.sql.Date(dateFrom.getTime()));
        params.put("p_date_to", dateTo != null ? new java.sql.Date(dateTo.getTime()) : null);

        return getOperationsTableJson.execute(params).get("returnvalue");
    }

    @Transactional
    public void recalculateIndexesByDate(Date date) {
        jdbcTemplate.update("call recalculate_indexes_by_date(?)", new java.sql.Date(date.getTime()));
    }
    @Transactional
    public void recalculateIndexesByDate(Date date, int startFrom) {
        jdbcTemplate.update("call recalculate_indexes_by_date(?, ?)", new java.sql.Date(date.getTime()), startFrom);
    }
    @Transactional
    public void recalculateIndexesByDate(Date date, int startFrom, int shiftSize) {
        jdbcTemplate.update("call recalculate_indexes_by_date(?, ?, ?)", new java.sql.Date(date.getTime()), startFrom, shiftSize);
    }

    public Long getPreviousAccountingObjectAmount(AccountingObject accountingObject, Date date, int currentIndexByDate) {
        return jdbcTemplate.queryForObject("select coalesce((select accounting_object_amount from (select row_number() over(order by o.date desc, o.index_by_date  desc), o.accounting_object_amount from operation o where o.accounting_object_id = ? and (o.date < ? or (o.date = ? and o.index_by_date < ?))) a where a.row_number = 1), (select ao.start_amount  from accounting_object ao where ao.id = ?))",  Long.class, accountingObject.getId(), date, date, currentIndexByDate, accountingObject.getId());
    }

    @Transactional
    public void recalculateAccountingObjectAmounts(AccountingObject accountingObject, Date rangeStartDate, int rangeStartIndexByDate) {
        jdbcTemplate.update("call recalculate_accounting_object_amounts(?, ?, ?)", accountingObject.getId(), new java.sql.Date(rangeStartDate.getTime()), rangeStartIndexByDate);
    }

    @Transactional
    public void recalculateAccountingObjectAmounts(AccountingObject accountingObject, Date rangeStartDate, int rangeStartIndexByDate, Date rangeFinishDate, int rangeFinishIndexByDate) {
        jdbcTemplate.update("call recalculate_accounting_object_amounts(?, ?, ?, ?, ?)", accountingObject.getId(), new java.sql.Date(rangeStartDate.getTime()), rangeStartIndexByDate, new java.sql.Date(rangeFinishDate.getTime()), rangeFinishIndexByDate);
    }

    public String getOperationAsJsonById(int id) {
//        return jdbcTemplate.queryForObject("select row from operation_as_json_object_view where id = ?", String.class, id);

        return jdbcTemplate.queryForObject("select row from get_operation_as_json_object(null, null, ?)", String.class, id);

    }

    public String getFactsRowAsJson() {

        return jdbcTemplate.queryForObject("select row from get_facts_row_as_json_object(null, null)", String.class);

    }

    public Integer getMaxIndexByDate(Date date) {

        return jdbcTemplate.queryForObject("select coalesce(max(index_by_date), 0) from operation where date = to_date(?, 'YYYY-mm-DD')", Integer.class, date);

    }

    @Transactional
    public void changeIds(int currentId, int id, Integer pairOperationId) {

        jdbcTemplate.update("call change_ids(?, ?, ?)", currentId, id, pairOperationId);

    }

    public Integer getNearbyId(int id, int direction) {
        // direction = -1 - предыдущая, 1 - следующая

        if (direction != -1 && direction != 1) {
            throw new RuntimeException("Функция getNearbyId: параметр direction отличается от -1 или 1");
        }

        return jdbcTemplate.queryForObject("select get_nearby_id(?, ?)", Integer.class, id, direction);
    }

    public Date getNearbyDate(int id, int direction) {
        // direction = -1 - предыдущая, 1 - следующая

        if (direction != -1 && direction != 1) {
            throw new RuntimeException("Функция getNearbyDate: параметр direction отличается от -1 или 1");
        }

        return jdbcTemplate.queryForObject("select get_nearby_date(?, ?)", Date.class, id, direction);
    }

    @Transactional
    public void shift(int id, int direction) {
        // direction = -1 - вверх, 1 - вниз

        if (direction != -1 && direction != 1) {
            throw new RuntimeException("Функция shift: параметр direction отличается от -1 или 1");
        }

        jdbcTemplate.update("call shift(?, ?)", id, direction);
    }

}

