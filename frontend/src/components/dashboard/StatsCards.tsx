/**
 * Dashboard statistics cards component
 */

import React from 'react';
import {
  Grid,
  Card,
  CardContent,
  Typography,
  Box,
  useTheme,
  Skeleton,
} from '@mui/material';
import {
  Assignment as TotalIcon,
  PlayArrow as ActiveIcon,
  CheckCircle as CompletedIcon,
  TrendingUp as TrendIcon,
} from '@mui/icons-material';
import { formatNumber, calculatePercentage } from '@/utils/helpers';
import type { TaskStatisticsResponseDTO } from '@/types/api';

interface StatsCardsProps {
  statistics?: TaskStatisticsResponseDTO;
  loading?: boolean;
}

interface StatCardProps {
  title: string;
  value: number;
  icon: React.ReactNode;
  color: string;
  subtitle?: string;
  trend?: {
    value: number;
    isPositive: boolean;
  };
}

const StatCard: React.FC<StatCardProps> = ({
  title,
  value,
  icon,
  color,
  subtitle,
  trend,
}) => {
  const theme = useTheme();

  return (
    <Card
      sx={{
        height: '100%',
        transition: 'transform 0.2s ease-in-out',
        '&:hover': {
          transform: 'translateY(-4px)',
          boxShadow: theme.shadows[8],
        },
      }}
    >
      <CardContent>
        <Box display="flex" alignItems="center" justifyContent="space-between">
          <Box flex={1}>
            <Typography
              variant="body2"
              color="text.secondary"
              sx={{ fontWeight: 500, mb: 1 }}
            >
              {title}
            </Typography>
            
            <Typography
              variant="h4"
              component="div"
              sx={{
                fontWeight: 700,
                color: theme.palette.text.primary,
                mb: subtitle ? 0.5 : 0,
              }}
            >
              {formatNumber(value)}
            </Typography>
            
            {subtitle && (
              <Typography variant="body2" color="text.secondary">
                {subtitle}
              </Typography>
            )}
            
            {trend && (
              <Box display="flex" alignItems="center" mt={1}>
                <TrendIcon
                  fontSize="small"
                  sx={{
                    color: trend.isPositive ? 'success.main' : 'error.main',
                    mr: 0.5,
                    transform: trend.isPositive ? 'none' : 'rotate(180deg)',
                  }}
                />
                <Typography
                  variant="caption"
                  sx={{
                    color: trend.isPositive ? 'success.main' : 'error.main',
                    fontWeight: 600,
                  }}
                >
                  {trend.value}%
                </Typography>
              </Box>
            )}
          </Box>
          
          <Box
            sx={{
              width: 56,
              height: 56,
              borderRadius: 2,
              backgroundColor: color + '15',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              color: color,
            }}
          >
            {icon}
          </Box>
        </Box>
      </CardContent>
    </Card>
  );
};

const StatCardSkeleton: React.FC = () => (
  <Card>
    <CardContent>
      <Box display="flex" alignItems="center" justifyContent="space-between">
        <Box flex={1}>
          <Skeleton variant="text" width="60%" height={20} />
          <Skeleton variant="text" width="40%" height={32} sx={{ my: 1 }} />
          <Skeleton variant="text" width="50%" height={16} />
        </Box>
        <Skeleton variant="rectangular" width={56} height={56} sx={{ borderRadius: 2 }} />
      </Box>
    </CardContent>
  </Card>
);

export const StatsCards: React.FC<StatsCardsProps> = ({
  statistics,
  loading = false,
}) => {
  const theme = useTheme();

  if (loading || !statistics) {
    return (
      <Grid container spacing={3}>
        {Array.from({ length: 4 }).map((_, index) => (
          <Grid item xs={12} sm={6} md={3} key={index}>
            <StatCardSkeleton />
          </Grid>
        ))}
      </Grid>
    );
  }

  const completionRate = calculatePercentage(
    statistics.completedTasks,
    statistics.totalTasks
  );

  const stats = [
    {
      title: 'Total Tasks',
      value: statistics.totalTasks,
      icon: <TotalIcon fontSize="large" />,
      color: theme.palette.primary.main,
      subtitle: 'All time',
    },
    {
      title: 'Active Tasks',
      value: statistics.activeTasks,
      icon: <ActiveIcon fontSize="large" />,
      color: theme.palette.warning.main,
      subtitle: 'In progress',
    },
    {
      title: 'Completed',
      value: statistics.completedTasks,
      icon: <CompletedIcon fontSize="large" />,
      color: theme.palette.success.main,
      subtitle: `${completionRate}% completion rate`,
    },
    {
      title: 'High Priority',
      value: statistics.tasksByPriority.HIGH || 0,
      icon: <TrendIcon fontSize="large" />,
      color: theme.palette.error.main,
      subtitle: 'Urgent tasks',
    },
  ];

  return (
    <Grid container spacing={3}>
      {stats.map((stat, index) => (
        <Grid item xs={12} sm={6} md={3} key={index}>
          <StatCard {...stat} />
        </Grid>
      ))}
    </Grid>
  );
};

export default StatsCards;
