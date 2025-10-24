/**
 * Main App component with theme provider and routing
 */

import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { ReactQueryDevtools } from '@tanstack/react-query-devtools';
import { ThemeProvider, CssBaseline, useMediaQuery } from '@mui/material';
import { Toaster } from 'react-hot-toast';
import { lightTheme, darkTheme } from '@/theme';
import { AppLayout } from '@/components/layout/AppLayout';
import { Dashboard } from '@/pages/Dashboard';
import { Tasks } from '@/pages/Tasks';
import { useUIStore } from '@/stores/uiStore';
import { CACHE_TIME, STALE_TIME } from '@/constants/api';

// Create a client
const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: STALE_TIME.MEDIUM,
      gcTime: CACHE_TIME.MEDIUM,
      retry: 2,
      refetchOnWindowFocus: false,
    },
    mutations: {
      retry: 1,
    },
  },
});

const AppContent: React.FC = () => {
  const { themeMode } = useUIStore();
  const prefersDarkMode = useMediaQuery('(prefers-color-scheme: dark)');

  // Determine the actual theme to use
  const isDarkMode = React.useMemo(() => {
    if (themeMode === 'system') {
      return prefersDarkMode;
    }
    return themeMode === 'dark';
  }, [themeMode, prefersDarkMode]);

  const theme = isDarkMode ? darkTheme : lightTheme;

  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <Router>
        <AppLayout>
          <Routes>
            <Route path="/" element={<Dashboard />} />
            <Route path="/dashboard" element={<Dashboard />} />
            <Route path="/tasks" element={<Tasks />} />
            {/* Add more routes here as needed */}
          </Routes>
        </AppLayout>
      </Router>

      {/* Toast notifications */}
      <Toaster
        position="top-right"
        toastOptions={{
          duration: 4000,
          style: {
            background: theme.palette.background.paper,
            color: theme.palette.text.primary,
            border: `1px solid ${theme.palette.divider}`,
            borderRadius: '8px',
            fontSize: '14px',
          },
          success: {
            iconTheme: {
              primary: theme.palette.success.main,
              secondary: theme.palette.success.contrastText,
            },
          },
          error: {
            iconTheme: {
              primary: theme.palette.error.main,
              secondary: theme.palette.error.contrastText,
            },
          },
        }}
      />
    </ThemeProvider>
  );
};

const App: React.FC = () => {
  return (
    <QueryClientProvider client={queryClient}>
      <AppContent />
      {/* React Query DevTools - only in development */}
      {import.meta.env.DEV && (
        <ReactQueryDevtools initialIsOpen={false} position="bottom" />
      )}
    </QueryClientProvider>
  );
};

export default App;
