import { useQueryState, parseAsInteger } from 'nuqs';
import useSWR from 'swr';
import { queryMonthlyExpense } from '../monthly-expenses-client';

export const MonthlyExpensePage = () => {
  const [year] = useQueryState('year', parseAsInteger.withDefault(2026));
  const [month] = useQueryState('month', parseAsInteger.withDefault(1));

  const { data: expense, isLoading, error } = useSWR(
    ['monthlyExpense', year, month],
    () => queryMonthlyExpense(year, month)
  );

  if (isLoading) return <div>Loading...</div>;
  if (error) return <div>Error: {error.message}</div>;
  if (!expense) return null;

  return (
    <div>
      <h1>{year}年{month}月の支出</h1>

      <section>
        <h2>カテゴリ別内訳</h2>
        <ul>
          {Object.entries(expense.breakdown).map(([category, amount]) => (
            <li key={category}>
              {category}: ¥{amount.toLocaleString()}
            </li>
          ))}
        </ul>
      </section>

      <section>
        <h2>合計</h2>
        <p>¥{expense.total.toLocaleString()}</p>
      </section>
    </div>
  );
};
