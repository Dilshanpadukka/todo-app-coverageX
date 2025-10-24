/**
 * Task card component for displaying individual tasks
 */

import React from 'react';
import {
  Card,
  CardContent,
  Typography,
  Box,
  Chip,
  IconButton,
  Menu,
  MenuItem,
  Tooltip,
  useTheme,
} from '@mui/material';
import {
  MoreVert as MoreIcon,
  Edit as EditIcon,
  Delete as DeleteIcon,
  PlayArrow as StartIcon,
  Pause as PauseIcon,
  CheckCircle as CompleteIcon,
  Schedule as ScheduleIcon,
} from '@mui/icons-material';
import { formatRelativeTime, getPriorityColor, getStatusColor } from '@/utils/helpers';
import type { TaskResponseDTO } from '@/types/api';

interface TaskCardProps {
  task: TaskResponseDTO;
  onEdit?: (task: TaskResponseDTO) => void;
  onDelete?: (taskId: number) => void;
  onStatusChange?: (taskId: number, statusId: number) => void;
  onView?: (task: TaskResponseDTO) => void;
  selected?: boolean;
  onSelect?: (taskId: number) => void;
}

export const TaskCard: React.FC<TaskCardProps> = ({
  task,
  onEdit,
  onDelete,
  onStatusChange,
  onView,
  selected = false,
  onSelect,
}) => {
  const theme = useTheme();
  const [anchorEl, setAnchorEl] = React.useState<null | HTMLElement>(null);
  const menuOpen = Boolean(anchorEl);

  const handleMenuOpen = (event: React.MouseEvent<HTMLElement>) => {
    event.stopPropagation();
    setAnchorEl(event.currentTarget);
  };

  const handleMenuClose = () => {
    setAnchorEl(null);
  };

  const handleEdit = () => {
    handleMenuClose();
    onEdit?.(task);
  };

  const handleDelete = () => {
    handleMenuClose();
    onDelete?.(task.id);
  };

  const handleStatusChange = (statusId: number) => {
    handleMenuClose();
    onStatusChange?.(task.id, statusId);
  };

  const handleCardClick = () => {
    if (onSelect) {
      onSelect(task.id);
    } else {
      onView?.(task);
    }
  };

  const getStatusIcon = (status: string) => {
    switch (status) {
      case 'IN_PROGRESS':
        return <StartIcon fontSize="small" />;
      case 'HOLD':
        return <PauseIcon fontSize="small" />;
      case 'DONE':
      case 'CLOSED':
        return <CompleteIcon fontSize="small" />;
      default:
        return <ScheduleIcon fontSize="small" />;
    }
  };

  const getQuickActions = () => {
    const actions = [];
    
    if (task.taskStatus.type !== 'IN_PROGRESS') {
      actions.push({
        label: 'Start',
        icon: <StartIcon fontSize="small" />,
        onClick: () => handleStatusChange(2), // Assuming IN_PROGRESS has ID 2
      });
    }
    
    if (task.taskStatus.type === 'IN_PROGRESS') {
      actions.push({
        label: 'Complete',
        icon: <CompleteIcon fontSize="small" />,
        onClick: () => handleStatusChange(4), // Assuming DONE has ID 4
      });
    }

    return actions;
  };

  return (
    <Card
      sx={{
        cursor: 'pointer',
        transition: 'all 0.2s ease-in-out',
        border: selected ? `2px solid ${theme.palette.primary.main}` : '1px solid transparent',
        '&:hover': {
          transform: 'translateY(-2px)',
          boxShadow: theme.shadows[4],
        },
      }}
      onClick={handleCardClick}
    >
      <CardContent sx={{ pb: 2 }}>
        {/* Header */}
        <Box display="flex" alignItems="flex-start" justifyContent="space-between" mb={2}>
          <Typography
            variant="h6"
            component="h3"
            sx={{
              fontWeight: 600,
              fontSize: '1.1rem',
              lineHeight: 1.3,
              flex: 1,
              mr: 1,
            }}
          >
            {task.taskTitle}
          </Typography>
          
          <IconButton
            size="small"
            onClick={handleMenuOpen}
            sx={{ ml: 1 }}
          >
            <MoreIcon fontSize="small" />
          </IconButton>
        </Box>

        {/* Description */}
        {task.description && (
          <Typography
            variant="body2"
            color="text.secondary"
            sx={{
              mb: 2,
              display: '-webkit-box',
              WebkitLineClamp: 2,
              WebkitBoxOrient: 'vertical',
              overflow: 'hidden',
              lineHeight: 1.4,
            }}
          >
            {task.description}
          </Typography>
        )}

        {/* Status and Priority Chips */}
        <Box display="flex" alignItems="center" gap={1} mb={2}>
          <Chip
            icon={getStatusIcon(task.taskStatus.type)}
            label={task.taskStatus.type.replace('_', ' ')}
            size="small"
            sx={{
              backgroundColor: getStatusColor(task.taskStatus.type as any),
              color: 'white',
              fontWeight: 500,
              '& .MuiChip-icon': {
                color: 'white',
              },
            }}
          />
          
          <Chip
            label={task.priority.type}
            size="small"
            variant="outlined"
            sx={{
              borderColor: getPriorityColor(task.priority.type as any),
              color: getPriorityColor(task.priority.type as any),
              fontWeight: 500,
            }}
          />
        </Box>

        {/* Footer */}
        <Box display="flex" alignItems="center" justifyContent="space-between">
          <Typography variant="caption" color="text.secondary">
            Updated {formatRelativeTime(task.lastStatusChangeDate)}
          </Typography>
          
          {/* Quick Actions */}
          <Box display="flex" gap={0.5}>
            {getQuickActions().map((action, index) => (
              <Tooltip key={index} title={action.label}>
                <IconButton
                  size="small"
                  onClick={(e) => {
                    e.stopPropagation();
                    action.onClick();
                  }}
                  sx={{
                    color: theme.palette.primary.main,
                    '&:hover': {
                      backgroundColor: theme.palette.primary.main + '10',
                    },
                  }}
                >
                  {action.icon}
                </IconButton>
              </Tooltip>
            ))}
          </Box>
        </Box>
      </CardContent>

      {/* Context Menu */}
      <Menu
        anchorEl={anchorEl}
        open={menuOpen}
        onClose={handleMenuClose}
        onClick={(e) => e.stopPropagation()}
        PaperProps={{
          sx: { minWidth: 160 },
        }}
      >
        <MenuItem onClick={handleEdit}>
          <EditIcon fontSize="small" sx={{ mr: 1 }} />
          Edit
        </MenuItem>
        
        <MenuItem onClick={() => handleStatusChange(2)}>
          <StartIcon fontSize="small" sx={{ mr: 1 }} />
          Start
        </MenuItem>
        
        <MenuItem onClick={() => handleStatusChange(4)}>
          <CompleteIcon fontSize="small" sx={{ mr: 1 }} />
          Complete
        </MenuItem>
        
        <MenuItem onClick={handleDelete} sx={{ color: 'error.main' }}>
          <DeleteIcon fontSize="small" sx={{ mr: 1 }} />
          Delete
        </MenuItem>
      </Menu>
    </Card>
  );
};

export default TaskCard;
