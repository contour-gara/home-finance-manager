# expenses-api

## create expense

### execute

```shell
curl -v -X POST http://localhost:28080/expense \
  -H "Content-Type: application/json" \
  -d '{
    "expenseId":"01K4MXEKC0PMTJ8FA055N4SH79",
    "amount":"100",
    "payer":"DIRECT_DEBIT",
    "category":"RENT",
    "year":"2026",
    "month":"1",
    "memo":"test"
  }'
```

### check

#### expense

```sql
SELECT
    e.expense_id,
    a.amount,
    ep.payer,
    ec.category,
    ey.year,
    em.month,
    memo.memo
FROM expense_id e
INNER JOIN expense_amount a ON e.expense_id = a.expense_id
INNER JOIN expense_payer ep ON e.expense_id = ep.expense_id
INNER JOIN expense_category ec ON e.expense_id = ec.expense_id
INNER JOIN expense_year ey ON e.expense_id = ey.expense_id
INNER JOIN expense_month em ON e.expense_id = em.expense_id
INNER JOIN expense_memo memo ON e.expense_id = memo.expense_id;
```

#### expense event

```sql
SELECT
    eei.expense_event_id,
    ee.expense_id,
    eec.event_category
FROM expense_event_id eei
INNER JOIN expense_event ee ON eei.expense_event_id = ee.expense_event_id
INNER JOIN expense_event_category eec ON eei.expense_event_id = eec.expense_event_id;
```

#### expenses

```sql
SELECT
    ey.last_event_id,
    ey.year,
    em.month,
    ep.payer,
    ec.category,
    ea.amount
FROM expenses_year ey
INNER JOIN expenses_month em ON ey.last_event_id = em.last_event_id
INNER JOIN expenses_payer ep ON ey.last_event_id = ep.last_event_id
INNER JOIN expenses_category ec ON ey.last_event_id = ec.last_event_id
INNER JOIN expenses_amount ea ON ey.last_event_id = ea.last_event_id;
```
