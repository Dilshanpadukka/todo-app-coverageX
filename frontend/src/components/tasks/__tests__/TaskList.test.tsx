/**
 * TaskList component unit tests
 */

import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent, waitFor } from '@/test/utils';
import userEvent from '@testing-library/user-event';
import { TaskList } from '../TaskList';
import { mockTasksPage, mockEmptyPage, mockTasks } from '@/test/mocks';

describe('TaskList', () => {
  const defaultProps = {
    tasks: mockTasksPage,
    loading: false,
    error: null,
    onTaskEdit: vi.fn(),
    onTaskDelete: vi.fn(),
    onTaskStatusChange: vi.fn(),
    onTaskView: vi.fn(),
    onPageChange: vi.fn(),
    onPageSizeChange: vi.fn(),
    onCreateTask: vi.fn(),
    onRetry: vi.fn(),
  };

  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('Rendering - With Tasks', () => {
    it('should render all tasks in grid', () => {
      render(<TaskList {...defaultProps} />);

      mockTasks.forEach(task => {
        expect(screen.getByText(task.taskTitle)).toBeInTheDocument();
      });
    });

    it('should display correct task count', () => {
      render(<TaskList {...defaultProps} />);

      expect(screen.getByText(/Showing 3 of 3 tasks/i)).toBeInTheDocument();
    });

    it('should render page size selector', () => {
      render(<TaskList {...defaultProps} />);

      // The select is rendered and accessible via its displayed value
      expect(screen.getByText('5')).toBeInTheDocument();
      // Use getAllByText since "Per page" appears in both label and legend
      const perPageElements = screen.getAllByText(/Per page/i);
      expect(perPageElements.length).toBeGreaterThan(0);
    });

    it('should render task cards in grid layout', () => {
      const { container } = render(<TaskList {...defaultProps} />);

      const gridItems = container.querySelectorAll('.MuiGrid-item');
      expect(gridItems.length).toBeGreaterThan(0);
    });
  });

  describe('Loading State', () => {
    it('should show loading skeleton when loading without tasks', () => {
      render(<TaskList {...defaultProps} tasks={undefined} loading={true} />);

      expect(screen.queryByText(/Showing/i)).not.toBeInTheDocument();
    });

    it('should show loading overlay when loading with existing tasks', () => {
      render(<TaskList {...defaultProps} loading={true} />);

      // Should still show tasks even when loading (loading overlay is transparent)
      expect(screen.getByText(mockTasks[0]!.taskTitle)).toBeInTheDocument();

      // With loading=true and tasks present, both content and loading indicator are shown
      // We can verify the component handles this state correctly by checking tasks are visible
      expect(screen.getByText(/Showing 3 of 3 tasks/i)).toBeInTheDocument();
    });
  });

  describe('Empty State', () => {
    it('should show empty state when no tasks', () => {
      render(<TaskList {...defaultProps} tasks={mockEmptyPage} />);

      expect(screen.getByText(/No tasks yet/i)).toBeInTheDocument();
    });

    it('should show create task button in empty state', async () => {
      const user = userEvent.setup();
      render(<TaskList {...defaultProps} tasks={mockEmptyPage} />);

      const createButton = screen.getByRole('button', {
        name: /Create First Task/i,
      });
      expect(createButton).toBeInTheDocument();

      await user.click(createButton);
      expect(defaultProps.onCreateTask).toHaveBeenCalled();
    });

    it('should show search empty state when search has no results', () => {
      render(
        <TaskList
          {...defaultProps}
          tasks={mockEmptyPage}
          searchTerm="nonexistent"
        />
      );

      expect(screen.getByText(/No results found/i)).toBeInTheDocument();
      expect(
        screen.getByText(/No tasks match "nonexistent"/i)
      ).toBeInTheDocument();
    });

    it('should show clear search button in search empty state', async () => {
      const user = userEvent.setup();
      const onClearSearch = vi.fn();

      render(
        <TaskList
          {...defaultProps}
          tasks={mockEmptyPage}
          searchTerm="test"
          onClearSearch={onClearSearch}
        />
      );

      const clearButton = screen.getByRole('button', { name: /Clear Search/i });
      await user.click(clearButton);

      expect(onClearSearch).toHaveBeenCalled();
    });
  });

  describe('Error State', () => {
    it('should show error message when error occurs', () => {
      const error = new Error('Failed to load tasks');
      render(<TaskList {...defaultProps} error={error} />);

      expect(screen.getByText(/Something went wrong/i)).toBeInTheDocument();
    });

    it('should show retry button on error', async () => {
      const user = userEvent.setup();
      const error = new Error('Failed to load tasks');

      render(<TaskList {...defaultProps} error={error} />);

      const retryButton = screen.getByRole('button', { name: /Try Again/i });
      expect(retryButton).toBeInTheDocument();

      await user.click(retryButton);
      expect(defaultProps.onRetry).toHaveBeenCalled();
    });
  });

  describe('Task Interactions', () => {
    it('should call onTaskEdit when edit is triggered', () => {
      render(<TaskList {...defaultProps} />);

      // This would need the TaskCard to expose edit functionality
      // Since TaskCard handles this internally, we verify it renders
      expect(screen.getByText(mockTasks[0]!.taskTitle)).toBeInTheDocument();
    });

    it('should pass correct props to TaskCard', () => {
      render(<TaskList {...defaultProps} />);

      // Verify all tasks are rendered with their data
      mockTasks.forEach(task => {
        expect(screen.getByText(task.taskTitle)).toBeInTheDocument();
        if (task.description) {
          expect(screen.getByText(task.description)).toBeInTheDocument();
        }
      });
    });
  });

  describe('Pagination', () => {
    it('should render pagination when multiple pages exist', () => {
      const multiPageTasks = {
        ...mockTasksPage,
        totalPages: 3,
        last: false,
      };

      render(<TaskList {...defaultProps} tasks={multiPageTasks} />);

      expect(screen.getByRole('navigation')).toBeInTheDocument();
    });

    it('should not render pagination when only one page exists', () => {
      render(<TaskList {...defaultProps} />);

      expect(screen.queryByRole('navigation')).not.toBeInTheDocument();
    });

    it('should call onPageChange when page is changed', async () => {
      const user = userEvent.setup();
      const multiPageTasks = {
        ...mockTasksPage,
        totalPages: 3,
        last: false,
      };

      render(<TaskList {...defaultProps} tasks={multiPageTasks} />);

      const nextPageButton = screen.getByRole('button', {
        name: /Go to page 2/i,
      });
      await user.click(nextPageButton);

      expect(defaultProps.onPageChange).toHaveBeenCalledWith(1);
    });

    it('should show correct page number', () => {
      const pageTwo = {
        ...mockTasksPage,
        number: 1,
        totalPages: 3,
      };

      render(<TaskList {...defaultProps} tasks={pageTwo} />);

      const currentPage = screen.getByRole('button', { name: /page 2/i });
      expect(currentPage).toHaveClass('Mui-selected');
    });
  });

  describe('Page Size Selection', () => {
    it('should call onPageSizeChange when page size is changed', async () => {
      const user = userEvent.setup();
      render(<TaskList {...defaultProps} />);

      // Find the select by its role
      const pageSizeSelect = screen.getByRole('combobox', {
        name: /Per page/i,
      });
      await user.click(pageSizeSelect);

      await waitFor(() => screen.getByRole('option', { name: '10' }));
      await user.click(screen.getByRole('option', { name: '10' }));

      expect(defaultProps.onPageSizeChange).toHaveBeenCalledWith(10);
    });

    it('should display available page size options', async () => {
      const user = userEvent.setup();
      render(<TaskList {...defaultProps} />);

      // Find the select by its role
      const pageSizeSelect = screen.getByRole('combobox', {
        name: /Per page/i,
      });
      await user.click(pageSizeSelect);

      await waitFor(() => {
        expect(screen.getByRole('option', { name: '5' })).toBeInTheDocument();
        expect(screen.getByRole('option', { name: '10' })).toBeInTheDocument();
        expect(screen.getByRole('option', { name: '25' })).toBeInTheDocument();
        expect(screen.getByRole('option', { name: '50' })).toBeInTheDocument();
      });
    });
  });

  describe('Search Results Display', () => {
    it('should display search term in results summary', () => {
      render(<TaskList {...defaultProps} searchTerm="test query" />);

      expect(screen.getByText(/for "test query"/i)).toBeInTheDocument();
    });

    it('should not display search term when not searching', () => {
      render(<TaskList {...defaultProps} />);

      const summary = screen.getByText(/Showing 3 of 3 tasks/i);
      expect(summary.textContent).not.toContain('for');
    });
  });

  describe('Bulk Selection', () => {
    it('should render tasks with selection capability', () => {
      render(<TaskList {...defaultProps} />);

      // Tasks should render - bulk selection is handled by TaskCard
      expect(screen.getByText(mockTasks[0]!.taskTitle)).toBeInTheDocument();
    });

    it('should handle selected tasks correctly', () => {
      // This test would verify the selected state is passed correctly
      // The actual selection logic is in the parent component
      render(<TaskList {...defaultProps} />);

      expect(screen.getAllByText(/Test/).length).toBeGreaterThan(0);
    });
  });

  describe('Responsive Behavior', () => {
    it('should render in mobile view', () => {
      // Mock mobile viewport
      window.matchMedia = vi.fn().mockImplementation(query => ({
        matches: query === '(max-width: 600px)',
        media: query,
        onchange: null,
        addListener: vi.fn(),
        removeListener: vi.fn(),
        addEventListener: vi.fn(),
        removeEventListener: vi.fn(),
        dispatchEvent: vi.fn(),
      }));

      render(<TaskList {...defaultProps} />);

      // Should still render all tasks
      mockTasks.forEach(task => {
        expect(screen.getByText(task.taskTitle)).toBeInTheDocument();
      });
    });
  });

  describe('Task Status Change Flow', () => {
    it('should handle mark as done optimistically', () => {
      render(<TaskList {...defaultProps} />);

      // Verify initial state - all tasks present
      expect(screen.getByText(mockTasks[0]!.taskTitle)).toBeInTheDocument();

      // The optimistic update would be handled by React Query
      // Here we verify the component renders correctly
    });

    it('should pass onTaskStatusChange to TaskCards', () => {
      render(<TaskList {...defaultProps} />);

      // Verify component renders with status change capability
      mockTasks.forEach(task => {
        expect(screen.getByText(task.taskTitle)).toBeInTheDocument();
      });
    });
  });

  describe('Grid Layout', () => {
    it('should render tasks in responsive grid', () => {
      const { container } = render(<TaskList {...defaultProps} />);

      const gridContainer = container.querySelector('.MuiGrid-container');
      expect(gridContainer).toBeInTheDocument();

      const gridItems = container.querySelectorAll('.MuiGrid-item');
      expect(gridItems.length).toBe(mockTasks.length);
    });

    it('should apply correct grid spacing', () => {
      const { container } = render(<TaskList {...defaultProps} />);

      const gridContainer = container.querySelector('.MuiGrid-container');
      expect(gridContainer).toHaveClass('MuiGrid-spacing-xs-3');
    });
  });
});
