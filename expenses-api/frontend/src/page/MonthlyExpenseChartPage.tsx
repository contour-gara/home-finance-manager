import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, Rectangle } from 'recharts';
import { useQueryState, parseAsInteger } from 'nuqs';
import useSWR from 'swr';
import { queryMonthlyExpense, ExpenseCategory } from '../monthly-expenses-client';

const categoryConfig: Record<ExpenseCategory, { label: string; color: string }> = {
  RENT: { label: '家賃', color: '#8884d8' },
  UTILITIES: { label: '光熱費', color: '#82ca9d' },
  FOOD: { label: '食費', color: '#ffc658' },
  DAILY_NEEDS: { label: '日用品', color: '#ff7300' },
  HEALTHCARE: { label: '医療費', color: '#00C49F' },
  ENTERTAINMENT: { label: '娯楽', color: '#FFBB28' },
  TRANSPORTATION: { label: '交通費', color: '#FF8042' },
  TRAVEL: { label: '旅行', color: '#0088FE' },
  OTHER: { label: 'その他', color: '#888888' },
};

const lightenColor = (hex: string, percent: number): string => {
  const num = parseInt(hex.replace('#', ''), 16);
  const r = Math.min(255, (num >> 16) + Math.round(255 * percent));
  const g = Math.min(255, ((num >> 8) & 0x00ff) + Math.round(255 * percent));
  const b = Math.min(255, (num & 0x0000ff) + Math.round(255 * percent));
  return `#${((r << 16) | (g << 8) | b).toString(16).padStart(6, '0')}`;
};

const transformData = (breakdown: Record<ExpenseCategory, number>) => {
  return Object.entries(breakdown).map(([category, amount]) => ({
    category: categoryConfig[category as ExpenseCategory].label,
    color: categoryConfig[category as ExpenseCategory].color,
    amount,
  }));
};

export const MonthlyExpenseChartPage = () => {
  const [year] = useQueryState('year', parseAsInteger.withDefault(2026));
  const [month] = useQueryState('month', parseAsInteger.withDefault(1));

  const { data: expense, isLoading, error } = useSWR(
    ['monthlyExpense', year, month],
    () => queryMonthlyExpense(year, month)
  );

  if (isLoading) return <div>Loading...</div>;
  if (error) return <div>Error: {error.message}</div>;
  if (!expense) return null;

  const chartData = transformData(expense.breakdown);

  return (
    <div>
      <h1>{year} 年 {month} 月の支出: ¥{expense.total.toLocaleString()}</h1>
      <BarChart
        width={700}
        height={433}
        data={chartData}
        margin={{ top: 5, right: 0, left: 0, bottom: 5 }}
      >
        <CartesianGrid strokeDasharray="3 3" />
        <XAxis dataKey="category" />
        <YAxis width={80} />
        <Tooltip />
        <Legend />
        <Bar
          dataKey="amount"
          name="支出額"
          radius={[10, 10, 0, 0]}
          shape={(props) => <Rectangle {...props} fill={props.payload.color} />}
          activeBar={(props) => <Rectangle {...props} fill={lightenColor(props.payload.color, 0.2)} />}
        />
      </BarChart>
    </div>
  );
};
