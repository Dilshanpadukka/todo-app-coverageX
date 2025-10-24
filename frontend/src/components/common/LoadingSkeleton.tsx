/**
 * Loading skeleton component for better UX during data fetching
 */

import React from 'react';
import {
  Skeleton,
  Card,
  CardContent,
  Box,
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableRow,
  Grid,
} from '@mui/material';
import type { SkeletonConfig } from '@/types/ui';

interface LoadingSkeletonProps {
  variant?: 'card' | 'table' | 'list' | 'stats' | 'chart';
  config?: SkeletonConfig;
}

/**
 * Card skeleton for dashboard cards
 */
const CardSkeleton: React.FC = () => (
  <Card>
    <CardContent>
      <Box display="flex" alignItems="center" mb={2}>
        <Skeleton variant="circular" width={40} height={40} />
        <Box ml={2} flex={1}>
          <Skeleton variant="text" width="60%" height={24} />
          <Skeleton variant="text" width="40%" height={20} />
        </Box>
      </Box>
      <Skeleton variant="text" width="80%" height={32} />
    </CardContent>
  </Card>
);

/**
 * Table skeleton for data tables
 */
const TableSkeleton: React.FC<{ config?: SkeletonConfig }> = ({ config }) => {
  const rows = config?.rows || 5;
  const columns = config?.columns || 4;
  const height = config?.height || 40;

  return (
    <Table>
      <TableHead>
        <TableRow>
          {Array.from({ length: columns }).map((_, index) => (
            <TableCell key={index}>
              <Skeleton variant="text" width="80%" height={20} />
            </TableCell>
          ))}
        </TableRow>
      </TableHead>
      <TableBody>
        {Array.from({ length: rows }).map((_, rowIndex) => (
          <TableRow key={rowIndex}>
            {Array.from({ length: columns }).map((_, colIndex) => (
              <TableCell key={colIndex}>
                <Skeleton variant="text" width="90%" height={height} />
              </TableCell>
            ))}
          </TableRow>
        ))}
      </TableBody>
    </Table>
  );
};

/**
 * List skeleton for task lists
 */
const ListSkeleton: React.FC<{ config?: SkeletonConfig }> = ({ config }) => {
  const rows = config?.rows || 5;

  return (
    <Box>
      {Array.from({ length: rows }).map((_, index) => (
        <Card key={index} sx={{ mb: 2 }}>
          <CardContent>
            <Box display="flex" alignItems="center" justifyContent="space-between">
              <Box flex={1}>
                <Skeleton variant="text" width="70%" height={24} />
                <Skeleton variant="text" width="90%" height={20} />
                <Box display="flex" alignItems="center" mt={1} gap={1}>
                  <Skeleton variant="rectangular" width={60} height={24} />
                  <Skeleton variant="rectangular" width={80} height={24} />
                  <Skeleton variant="text" width="30%" height={16} />
                </Box>
              </Box>
              <Box display="flex" gap={1}>
                <Skeleton variant="circular" width={32} height={32} />
                <Skeleton variant="circular" width={32} height={32} />
              </Box>
            </Box>
          </CardContent>
        </Card>
      ))}
    </Box>
  );
};

/**
 * Statistics skeleton for dashboard stats
 */
const StatsSkeleton: React.FC = () => (
  <Grid container spacing={3}>
    {Array.from({ length: 4 }).map((_, index) => (
      <Grid item xs={12} sm={6} md={3} key={index}>
        <Card>
          <CardContent>
            <Box display="flex" alignItems="center" justifyContent="space-between">
              <Box>
                <Skeleton variant="text" width={80} height={20} />
                <Skeleton variant="text" width={60} height={32} />
              </Box>
              <Skeleton variant="circular" width={48} height={48} />
            </Box>
          </CardContent>
        </Card>
      </Grid>
    ))}
  </Grid>
);

/**
 * Chart skeleton for charts and graphs
 */
const ChartSkeleton: React.FC = () => (
  <Card>
    <CardContent>
      <Skeleton variant="text" width="40%" height={24} sx={{ mb: 2 }} />
      <Box display="flex" alignItems="end" justifyContent="space-around" height={200}>
        {Array.from({ length: 5 }).map((_, index) => (
          <Skeleton
            key={index}
            variant="rectangular"
            width={40}
            height={Math.random() * 150 + 50}
          />
        ))}
      </Box>
    </CardContent>
  </Card>
);

/**
 * Main loading skeleton component
 */
export const LoadingSkeleton: React.FC<LoadingSkeletonProps> = ({
  variant = 'card',
  config,
}) => {
  switch (variant) {
    case 'table':
      return <TableSkeleton config={config} />;
    case 'list':
      return <ListSkeleton config={config} />;
    case 'stats':
      return <StatsSkeleton />;
    case 'chart':
      return <ChartSkeleton />;
    case 'card':
    default:
      return <CardSkeleton />;
  }
};

export default LoadingSkeleton;
