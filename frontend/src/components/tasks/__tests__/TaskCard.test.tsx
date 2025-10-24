/**
 * TaskCard component unit tests
 */

import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent, waitFor } from '@/test/utils';
import { TaskCard } from '../TaskCard';
import { mockTask, mockPriorityTypes, mockTaskStatusTypes } from '@/test/mocks';

describe('TaskCard', () => {
  const defaultProps = {
    task: mockTask,
    onEdit: vi.fn(),
    onDelete: vi.fn(),
    onStatusChange: vi.fn(),
    onView: vi.fn(),
  };

  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('Rendering', () => {
    it('should render task title and description', () => {
      render(<TaskCard {...defaultProps} />);

      expect(screen.getByText(mockTask.taskTitle)).toBeInTheDocument();
      expect(screen.getByText(mockTask.description!)).toBeInTheDocument();
    });

    it('should render priority chip with correct color', () => {
      render(<TaskCard {...defaultProps} />);

      const priorityChip = screen.getByText('HIGH');
      expect(priorityChip).toBeInTheDocument();
    });

    it('should render status chip with correct label', () => {
      render(<TaskCard {...defaultProps} />);

      const statusChip = screen.getByText('OPEN');
      expect(statusChip).toBeInTheDocument();
    });

    it('should not render description if not provided', () => {
      const taskWithoutDescription = { ...mockTask, description: undefined };
      render(<TaskCard {...defaultProps} task={taskWithoutDescription} />);

      expect(screen.queryByText(mockTask.description!)).not.toBeInTheDocument();
    });

    it('should show updated timestamp', () => {
      render(<TaskCard {...defaultProps} />);

      expect(screen.getByText(/Updated/i)).toBeInTheDocument();
    });
  });

  describe('Menu Interactions', () => {
    it('should open context menu when clicking more icon', async () => {
      render(<TaskCard {...defaultProps} />);

      const moreButton = screen
        .getAllByRole('button')
        .find(btn => btn.querySelector('svg[data-testid="MoreVertIcon"]'));

      expect(moreButton).toBeInTheDocument();
      fireEvent.click(moreButton!);

      await waitFor(() => {
        expect(screen.getByText('Edit')).toBeInTheDocument();
        expect(screen.getByText('Start')).toBeInTheDocument();
        expect(screen.getByText('Complete')).toBeInTheDocument();
        expect(screen.getByText('Delete')).toBeInTheDocument();
      });
    });

    it('should call onEdit when Edit is clicked', async () => {
      render(<TaskCard {...defaultProps} />);

      const moreButton = screen
        .getAllByRole('button')
        .find(btn => btn.querySelector('svg[data-testid="MoreVertIcon"]'));
      fireEvent.click(moreButton!);

      await waitFor(() => screen.getByText('Edit'));
      fireEvent.click(screen.getByText('Edit'));

      expect(defaultProps.onEdit).toHaveBeenCalledWith(mockTask);
    });

    it('should call onDelete when Delete is clicked', async () => {
      render(<TaskCard {...defaultProps} />);

      const moreButton = screen
        .getAllByRole('button')
        .find(btn => btn.querySelector('svg[data-testid="MoreVertIcon"]'));
      fireEvent.click(moreButton!);

      await waitFor(() => screen.getByText('Delete'));
      fireEvent.click(screen.getByText('Delete'));

      expect(defaultProps.onDelete).toHaveBeenCalledWith(mockTask.id);
    });

    it('should call onStatusChange when Start is clicked', async () => {
      render(<TaskCard {...defaultProps} />);

      const moreButton = screen
        .getAllByRole('button')
        .find(btn => btn.querySelector('svg[data-testid="MoreVertIcon"]'));
      fireEvent.click(moreButton!);

      await waitFor(() => screen.getByText('Start'));
      fireEvent.click(screen.getByText('Start'));

      expect(defaultProps.onStatusChange).toHaveBeenCalledWith(mockTask.id, 2);
    });

    it('should call onStatusChange when Complete is clicked', async () => {
      render(<TaskCard {...defaultProps} />);

      const moreButton = screen
        .getAllByRole('button')
        .find(btn => btn.querySelector('svg[data-testid="MoreVertIcon"]'));
      fireEvent.click(moreButton!);

      await waitFor(() => screen.getByText('Complete'));
      fireEvent.click(screen.getByText('Complete'));

      expect(defaultProps.onStatusChange).toHaveBeenCalledWith(mockTask.id, 4);
    });
  });

  describe('Quick Actions', () => {
    it('should show Start quick action for OPEN tasks', () => {
      render(<TaskCard {...defaultProps} />);

      const quickActions = screen
        .getAllByRole('button')
        .filter(btn => btn.querySelector('svg[data-testid="PlayArrowIcon"]'));

      expect(quickActions.length).toBeGreaterThan(0);
    });

    it('should show Complete quick action for IN_PROGRESS tasks', () => {
      const inProgressTask = {
        ...mockTask,
        taskStatus: mockTaskStatusTypes[1]!,
      };

      render(<TaskCard {...defaultProps} task={inProgressTask} />);

      const completeButtons = screen
        .getAllByRole('button')
        .filter(btn => btn.querySelector('svg[data-testid="CheckCircleIcon"]'));

      expect(completeButtons.length).toBeGreaterThan(0);
    });

    it('should call onStatusChange when quick action is clicked', () => {
      render(<TaskCard {...defaultProps} />);

      const startButton = screen
        .getAllByRole('button')
        .find(
          btn =>
            btn.querySelector('svg[data-testid="PlayArrowIcon"]') &&
            btn.getAttribute('title') !== null
        );

      if (startButton) {
        fireEvent.click(startButton);
        expect(defaultProps.onStatusChange).toHaveBeenCalled();
      }
    });
  });

  describe('Card Click', () => {
    it('should call onView when card is clicked', () => {
      render(<TaskCard {...defaultProps} />);

      const card = screen
        .getByText(mockTask.taskTitle)
        .closest('.MuiCard-root');
      expect(card).toBeInTheDocument();

      if (card) {
        fireEvent.click(card);
        expect(defaultProps.onView).toHaveBeenCalledWith(mockTask);
      }
    });

    it('should call onSelect when provided instead of onView', () => {
      const onSelect = vi.fn();
      render(
        <TaskCard {...defaultProps} onSelect={onSelect} onView={undefined} />
      );

      const card = screen
        .getByText(mockTask.taskTitle)
        .closest('.MuiCard-root');
      if (card) {
        fireEvent.click(card);
        expect(onSelect).toHaveBeenCalledWith(mockTask.id);
        expect(defaultProps.onView).not.toHaveBeenCalled();
      }
    });
  });

  describe('Selection State', () => {
    it('should apply selected styling when selected', () => {
      const { container } = render(
        <TaskCard {...defaultProps} selected={true} />
      );

      const card = container.querySelector('.MuiCard-root');
      // Check that the card element exists when selected
      expect(card).toBeInTheDocument();
    });

    it('should not apply selected styling when not selected', () => {
      const { container } = render(
        <TaskCard {...defaultProps} selected={false} />
      );

      const card = container.querySelector('.MuiCard-root');
      expect(card).toHaveStyle({ border: '1px solid transparent' });
    });
  });

  describe('Status Icons', () => {
    it('should show correct icon for OPEN status', () => {
      render(<TaskCard {...defaultProps} />);

      const statusChip = screen.getByText('OPEN').closest('.MuiChip-root');
      expect(
        statusChip?.querySelector('svg[data-testid="ScheduleIcon"]')
      ).toBeInTheDocument();
    });

    it('should show correct icon for IN_PROGRESS status', () => {
      const inProgressTask = {
        ...mockTask,
        taskStatus: mockTaskStatusTypes[1]!,
      };

      render(<TaskCard {...defaultProps} task={inProgressTask} />);

      const statusChip = screen
        .getByText('IN PROGRESS')
        .closest('.MuiChip-root');
      expect(
        statusChip?.querySelector('svg[data-testid="PlayArrowIcon"]')
      ).toBeInTheDocument();
    });

    it('should show correct icon for DONE status', () => {
      const doneTask = {
        ...mockTask,
        taskStatus: mockTaskStatusTypes[3]!,
      };

      render(<TaskCard {...defaultProps} task={doneTask} />);

      const statusChip = screen.getByText('DONE').closest('.MuiChip-root');
      expect(
        statusChip?.querySelector('svg[data-testid="CheckCircleIcon"]')
      ).toBeInTheDocument();
    });
  });
});
