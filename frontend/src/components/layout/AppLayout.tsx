/**
 * Main application layout component
 */

import React from 'react';
import {
  Box,
  AppBar,
  Toolbar,
  Typography,
  IconButton,
  useTheme,
  useMediaQuery,
  Container,
} from '@mui/material';
import {
  Menu as MenuIcon,
  Brightness4 as DarkModeIcon,
  Brightness7 as LightModeIcon,
  Dashboard as DashboardIcon,
} from '@mui/icons-material';
import { useUIStore } from '@/stores/uiStore';

interface AppLayoutProps {
  children: React.ReactNode;
}

export const AppLayout: React.FC<AppLayoutProps> = ({ children }) => {
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('md'));
  
  const {
    themeMode,
    setThemeMode,
    sidebarOpen,
    toggleSidebar,
  } = useUIStore();

  const handleThemeToggle = () => {
    setThemeMode(themeMode === 'light' ? 'dark' : 'light');
  };

  return (
    <Box sx={{ display: 'flex', flexDirection: 'column', minHeight: '100vh' }}>
      {/* App Bar */}
      <AppBar
        position="sticky"
        elevation={0}
        sx={{
          backgroundColor: theme.palette.background.paper,
          borderBottom: `1px solid ${theme.palette.divider}`,
          color: theme.palette.text.primary,
        }}
      >
        <Toolbar>
          {isMobile && (
            <IconButton
              edge="start"
              color="inherit"
              aria-label="menu"
              onClick={toggleSidebar}
              sx={{ mr: 2 }}
            >
              <MenuIcon />
            </IconButton>
          )}
          
          <Box sx={{ display: 'flex', alignItems: 'center', flexGrow: 1 }}>
            <DashboardIcon sx={{ mr: 1, color: theme.palette.primary.main }} />
            <Typography
              variant="h6"
              component="h1"
              sx={{
                fontWeight: 600,
                color: theme.palette.text.primary,
              }}
            >
              Task Manager
            </Typography>
          </Box>

          <IconButton
            color="inherit"
            onClick={handleThemeToggle}
            aria-label="toggle theme"
            sx={{
              ml: 1,
              color: theme.palette.text.secondary,
              '&:hover': {
                color: theme.palette.text.primary,
              },
            }}
          >
            {themeMode === 'light' ? <DarkModeIcon /> : <LightModeIcon />}
          </IconButton>
        </Toolbar>
      </AppBar>

      {/* Main Content */}
      <Box
        component="main"
        sx={{
          flexGrow: 1,
          backgroundColor: theme.palette.background.default,
          minHeight: 'calc(100vh - 64px)',
        }}
      >
        <Container
          maxWidth="xl"
          sx={{
            py: 3,
            px: { xs: 2, sm: 3 },
          }}
        >
          {children}
        </Container>
      </Box>

      {/* Footer */}
      {/* <Box
        component="footer"
        sx={{
          py: 2,
          px: 3,
          backgroundColor: theme.palette.background.paper,
          borderTop: `1px solid ${theme.palette.divider}`,
          textAlign: 'center',
        }}
      >
        <Typography
          variant="body2"
          color="text.secondary"
          sx={{ fontSize: '0.875rem' }}
        >
          © 2024 Task Manager. Built with React, TypeScript, and Material-UI.
        </Typography>
      </Box> */}
    </Box>
  );
};

export default AppLayout;
