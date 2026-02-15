import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import { BrowserRouter, Navigate, Routes, Route } from 'react-router'
import { NuqsAdapter } from 'nuqs/adapters/react-router/v7'
import { MonthlyExpensePage } from './page/MonthlyExpensePage'
import { MonthlyExpenseChartPage } from './page/MonthlyExpenseChartPage'

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <BrowserRouter>
      <NuqsAdapter>
        <Routes>
          <Route index element={<Navigate replace to='monthly-expenses'/>} />
          <Route path='monthly-expenses' element={<MonthlyExpensePage />} />
          <Route path='monthly-expenses-chart' element={<MonthlyExpenseChartPage />} />
        </Routes>
      </NuqsAdapter>
    </BrowserRouter>
  </StrictMode>
)
