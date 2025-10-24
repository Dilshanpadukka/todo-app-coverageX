/**
 * Zustand store for UI state management
 */

import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import { LOCAL_STORAGE_KEYS, DEFAULT_PAGE_SIZE } from '@/constants/ui';
import type { ThemeMode, FilterPanelState, BulkActionConfig } from '@/types/ui';

interface UIState {
  // Theme
  themeMode: ThemeMode['mode'];
  setThemeMode: (mode: ThemeMode['mode']) => void;

  // Loading states
  isLoading: boolean;
  setIsLoading: (loading: boolean) => void;

  // Page size preference
  pageSize: number;
  setPageSize: (size: number) => void;

  // Filter panel
  filterPanel: FilterPanelState;
  setFilterPanel: (state: Partial<FilterPanelState>) => void;
  toggleFilterPanel: () => void;

  // Bulk actions
  bulkActions: BulkActionConfig;
  setBulkActions: (config: Partial<BulkActionConfig>) => void;
  clearBulkSelection: () => void;
  toggleBulkSelection: (id: number) => void;
  selectAllBulk: (ids: number[]) => void;

  // Modals
  modals: {
    createTask: boolean;
    editTask: boolean;
    taskDetail: boolean;
    confirmDelete: boolean;
  };
  setModal: (modal: keyof UIState['modals'], open: boolean) => void;

  // Selected items
  selectedTaskId: number | null;
  setSelectedTaskId: (id: number | null) => void;

  // Sidebar (mobile)
  sidebarOpen: boolean;
  setSidebarOpen: (open: boolean) => void;
  toggleSidebar: () => void;
}

export const useUIStore = create<UIState>()(
  persist(
    (set, get) => ({
      // Theme
      themeMode: 'system',
      setThemeMode: (mode) => set({ themeMode: mode }),

      // Loading states
      isLoading: false,
      setIsLoading: (loading) => set({ isLoading: loading }),

      // Page size preference
      pageSize: DEFAULT_PAGE_SIZE,
      setPageSize: (size) => set({ pageSize: size }),

      // Filter panel
      filterPanel: {
        open: false,
        statusFilters: [],
        priorityFilter: undefined,
        dateRange: undefined,
      },
      setFilterPanel: (state) => 
        set((prev) => ({ 
          filterPanel: { ...prev.filterPanel, ...state } 
        })),
      toggleFilterPanel: () => 
        set((prev) => ({ 
          filterPanel: { ...prev.filterPanel, open: !prev.filterPanel.open } 
        })),

      // Bulk actions
      bulkActions: {
        selectedIds: [],
        availableActions: [],
      },
      setBulkActions: (config) => 
        set((prev) => ({ 
          bulkActions: { ...prev.bulkActions, ...config } 
        })),
      clearBulkSelection: () => 
        set((prev) => ({ 
          bulkActions: { ...prev.bulkActions, selectedIds: [] } 
        })),
      toggleBulkSelection: (id) => 
        set((prev) => {
          const selectedIds = prev.bulkActions.selectedIds;
          const newSelectedIds = selectedIds.includes(id)
            ? selectedIds.filter(selectedId => selectedId !== id)
            : [...selectedIds, id];
          
          return {
            bulkActions: { ...prev.bulkActions, selectedIds: newSelectedIds }
          };
        }),
      selectAllBulk: (ids) => 
        set((prev) => ({ 
          bulkActions: { ...prev.bulkActions, selectedIds: ids } 
        })),

      // Modals
      modals: {
        createTask: false,
        editTask: false,
        taskDetail: false,
        confirmDelete: false,
      },
      setModal: (modal, open) => 
        set((prev) => ({ 
          modals: { ...prev.modals, [modal]: open } 
        })),

      // Selected items
      selectedTaskId: null,
      setSelectedTaskId: (id) => set({ selectedTaskId: id }),

      // Sidebar (mobile)
      sidebarOpen: false,
      setSidebarOpen: (open) => set({ sidebarOpen: open }),
      toggleSidebar: () => set((prev) => ({ sidebarOpen: !prev.sidebarOpen })),
    }),
    {
      name: LOCAL_STORAGE_KEYS.THEME_MODE,
      partialize: (state) => ({
        themeMode: state.themeMode,
        pageSize: state.pageSize,
        filterPanel: {
          statusFilters: state.filterPanel.statusFilters,
          priorityFilter: state.filterPanel.priorityFilter,
        },
      }),
    }
  )
);
