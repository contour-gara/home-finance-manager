import { http, HttpResponse } from 'msw';
import { setupServer } from 'msw/node';
import { describe, it, expect, beforeAll, afterAll, afterEach } from 'vitest';
import {
  queryMonthlyExpense,
  queryMonthlyExpenseByPayer,
  type MonthlyExpense,
} from './index';

const mockResponse: MonthlyExpense = {
  breakdown: {
    RENT: 0,
    UTILITIES: 0,
    FOOD: 41596,
    DAILY_NEEDS: 7192,
    HEALTHCARE: 1009,
    ENTERTAINMENT: 20990,
    TRANSPORTATION: 0,
    TRAVEL: 70400,
    OTHER: 0,
  },
  total: 141187,
};

const server = setupServer(
  http.get('/expenses/:year/:month', ({ request }) => {
    const url = new URL(request.url);
    const payer = url.searchParams.get('payer');

    // payer ありの場合は別のレスポンスを返す例
    if (payer === 'gara') {
      return HttpResponse.json({
        breakdown: { ...mockResponse.breakdown, FOOD: 20000 },
        total: 100000,
      });
    }

    return HttpResponse.json(mockResponse);
  })
);

beforeAll(() => server.listen());
afterEach(() => server.resetHandlers());
afterAll(() => server.close());

describe('queryMonthlyExpense', () => {
  it('指定した年月の支出サマリーを取得できる', async () => {
    const result = await queryMonthlyExpense(2024, 6);

    expect(result.total).toBe(141187);
    expect(result.breakdown.FOOD).toBe(41596);
    expect(result.breakdown.TRAVEL).toBe(70400);
  });
});

describe('queryMonthlyExpenseByPayer', () => {
  it('payer を指定して支出サマリーを取得できる', async () => {
    const result = await queryMonthlyExpenseByPayer(2024, 6, 'gara');

    expect(result.total).toBe(100000);
    expect(result.breakdown.FOOD).toBe(20000);
  });

  it('クエリパラメータが正しく送信される', async () => {
    let capturedPayer: string | null = null;

    server.use(
      http.get('/expenses/:year/:month', ({ request }) => {
        const url = new URL(request.url);
        capturedPayer = url.searchParams.get('payer');
        return HttpResponse.json(mockResponse);
      })
    );

    await queryMonthlyExpenseByPayer(2024, 6, 'alice');

    expect(capturedPayer).toBe('alice');
  });
});
