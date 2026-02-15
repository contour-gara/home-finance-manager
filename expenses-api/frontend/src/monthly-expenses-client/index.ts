import ky from "ky";

const query = ky.create({
  prefixUrl: '/expenses',
  timeout: 10000,
});

export const ExpenseCategory = {
  RENT: 'RENT',
  UTILITIES: 'UTILITIES',
  FOOD: 'FOOD',
  DAILY_NEEDS: 'DAILY_NEEDS',
  HEALTHCARE: 'HEALTHCARE',
  ENTERTAINMENT: 'ENTERTAINMENT',
  TRANSPORTATION: 'TRANSPORTATION',
  TRAVEL: 'TRAVEL',
  OTHER: 'OTHER',
} as const;

export type ExpenseCategory = (typeof ExpenseCategory)[keyof typeof ExpenseCategory];

export type MonthlyExpense = {
  breakdown: Record<ExpenseCategory, number>;
  total: number;
};

export const queryMonthlyExpense = (year: number, month: number): Promise<MonthlyExpense> => {
  return query.get(`${year}/${month}`).json<MonthlyExpense>();
};

export const queryMonthlyExpenseByPayer = (
  year: number,
  month: number,
  payer: string
): Promise<MonthlyExpense> => {
  return query
    .get(`${year}/${month}`, {
      searchParams: { payer },
    })
    .json<MonthlyExpense>();
};
