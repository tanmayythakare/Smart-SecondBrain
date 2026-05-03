import { Component, OnInit, ViewEncapsulation, AfterViewChecked } from '@angular/core';
import { TaskService, Task } from '../task.service';

@Component({
  selector: 'app-task-list',
  templateUrl: './task-list.component.html',
  styleUrls: ['./task-list.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class TaskListComponent implements OnInit, AfterViewChecked {

  ngAfterViewChecked(): void {
    if ((window as any).lucide) {
      (window as any).lucide.createIcons();
    }
  }

  tasks: Task[] = [];
  get activeCount(): number {
    return this.tasks.filter(t => t.status !== 'DONE').length;
  }
  newTaskTitle = '';
  newTaskPriority: string = 'MEDIUM';
  newTaskDueDate: string = '';
  // Custom Calendar properties
  showCalendar = false;
  calendarDays: Date[] = [];
  calendarEmptyDays: number[] = [];
  currentViewDate = new Date();
  
  get currentMonthName(): string {
    return this.currentViewDate.toLocaleString('default', { month: 'long' });
  }
  
  get currentYear(): number {
    return this.currentViewDate.getFullYear();
  }
  showTaskForm = false; // controls expanded form

  // Edit mode properties
  editingTaskId: number | null = null;
  editingTaskTitle = '';

  // Delete confirmation
  deleteConfirmTaskId: number | null = null;

  // loading & error states
  isLoading = false;
  errorMessage: string | null = null;

  constructor(private taskService: TaskService) { }

  ngOnInit(): void {
    this.loadTasks();
  }

  loadTasks() {
    this.isLoading = true;
    this.errorMessage = null;

    this.taskService.getTasks().subscribe({
      next: (tasks) => {
        this.tasks = tasks;
        this.isLoading = false;
      },
      error: (error) => {
        console.error(error);
        this.errorMessage = 'Failed to load tasks. Please try again.';
        this.isLoading = false;
      }
    });
  }

  // ============================================
  // ADD TASK
  // ============================================

  addTask() {
    if (!this.newTaskTitle.trim()) {
      return;
    }

    this.isLoading = true;
    this.errorMessage = null;

    this.taskService.createTask(
      this.newTaskTitle,
      this.newTaskPriority,
      this.newTaskDueDate || null
    ).subscribe({
      next: () => {
        this.showSuccess('Task created successfully');
        this.newTaskTitle = '';
        this.newTaskPriority = 'MEDIUM';
        this.newTaskDueDate = '';
        this.showTaskForm = false;
        this.loadTasks();
      },
      error: (error) => {
        console.error(error);
        this.errorMessage = 'Failed to create task.';
        this.isLoading = false;
      }
    });
  }

  // ============================================
  // EDIT TASK
  // ============================================

  startEdit(task: Task) {
    this.editingTaskId = task.id!;
    this.editingTaskTitle = task.title;

    setTimeout(() => {
      const input = document.querySelector(
        `input[data-task-id="${task.id}"]`
      ) as HTMLInputElement;

      if (input) {
        input.focus();
        input.select();
      }
    }, 50);
  }

  isEditing(task: Task): boolean {
    return this.editingTaskId === task.id;
  }

  successMessage: string | null = null;

  showSuccess(message: string) {
    this.successMessage = message;
    setTimeout(() => {
      this.successMessage = null;
    }, 3000);
  }

  saveEdit(task: Task) {
    if (!this.editingTaskTitle.trim()) {
      this.cancelEdit();
      return;
    }

    this.isLoading = true;
    this.errorMessage = null;

    this.taskService.updateTask(task.id!, this.editingTaskTitle, task.status, task.priority, task.dueDate).subscribe({
      next: () => {
        this.showSuccess('Task updated successfully');
        this.cancelEdit();
        this.loadTasks();
      },
      error: (error) => {
        console.error(error);
        this.errorMessage = 'Failed to update task.';
        this.isLoading = false;
      }
    });
  }
  isOverdue(dueDate: string): boolean {
    return new Date(dueDate) < new Date();
  }

  cancelEdit() {
    this.editingTaskId = null;
    this.editingTaskTitle = '';
  }

  onEditKeydown(event: KeyboardEvent, task: Task) {
    if (event.key === 'Enter') {
      event.preventDefault();
      this.saveEdit(task);
    } else if (event.key === 'Escape') {
      this.cancelEdit();
    }
  }

  toggleTaskStatus(task: Task) {
    this.isLoading = true;
    const newStatus = task.status === 'DONE' ? 'TODO' : 'DONE';
    this.taskService.updateTask(task.id!, task.title, newStatus, task.priority, task.dueDate).subscribe({
      next: () => {
        this.showSuccess('Task status updated');
        this.loadTasks();
      },
      error: (error) => {
        console.error(error);
        this.errorMessage = 'Failed to update task status.';
        this.isLoading = false;
      }
    });
  }

  // ============================================
  // DELETE TASK
  // ============================================

  toggleDeleteConfirm(task: Task) {
    this.deleteConfirmTaskId =
      this.deleteConfirmTaskId === task.id ? null : task.id!;
  }

  isDeleteConfirmVisible(task: Task): boolean {
    return this.deleteConfirmTaskId === task.id;
  }

  confirmDelete() {
    if (!this.deleteConfirmTaskId) return;
    this.isLoading = true;
    this.errorMessage = null;

    this.taskService.deleteTask(this.deleteConfirmTaskId).subscribe({
      next: () => {
        this.showSuccess('Task deleted successfully');
        this.deleteConfirmTaskId = null;
        this.loadTasks();
      },
      error: (error) => {
        console.error(error);
        this.errorMessage = 'Failed to delete task.';
        this.isLoading = false;
      }
    });
  }

  cancelDelete() {
    this.deleteConfirmTaskId = null;
  }

  // ============================================
  // CUSTOM CALENDAR LOGIC
  // ============================================

  toggleCalendar() {
    this.showCalendar = !this.showCalendar;
    if (this.showCalendar) {
      this.generateCalendar();
      // Ensure icons render in the new popup
      setTimeout(() => {
        if ((window as any).lucide) {
          (window as any).lucide.createIcons();
        }
      }, 0);
    }
  }

  generateCalendar() {
    const year = this.currentViewDate.getFullYear();
    const month = this.currentViewDate.getMonth();
    
    // First day of month
    const firstDay = new Date(year, month, 1);
    // Number of empty slots (Mon=0, Sun=6)
    let dayOfWeek = firstDay.getDay(); // Sun=0, Mon=1...
    let emptySlots = dayOfWeek === 0 ? 6 : dayOfWeek - 1;
    this.calendarEmptyDays = Array(emptySlots).fill(0);

    // Days in month
    const lastDay = new Date(year, month + 1, 0);
    const days: Date[] = [];
    for (let i = 1; i <= lastDay.getDate(); i++) {
      days.push(new Date(year, month, i));
    }
    this.calendarDays = days;
  }

  prevMonth(event: Event) {
    event.stopPropagation();
    this.currentViewDate = new Date(this.currentViewDate.getFullYear(), this.currentViewDate.getMonth() - 1, 1);
    this.generateCalendar();
  }

  nextMonth(event: Event) {
    event.stopPropagation();
    this.currentViewDate = new Date(this.currentViewDate.getFullYear(), this.currentViewDate.getMonth() + 1, 1);
    this.generateCalendar();
  }

  selectDate(date: Date, event: Event) {
    event.stopPropagation();
    // Format to YYYY-MM-DD
    const y = date.getFullYear();
    const m = (date.getMonth() + 1).toString().padStart(2, '0');
    const d = date.getDate().toString().padStart(2, '0');
    this.newTaskDueDate = `${y}-${m}-${d}`;
    this.showCalendar = false;
  }

  selectPreset(type: 'today' | 'tomorrow' | 'none', event: Event) {
    event.stopPropagation();
    const now = new Date();
    if (type === 'today') {
      this.selectDate(now, event);
    } else if (type === 'tomorrow') {
      const tomorrow = new Date(now);
      tomorrow.setDate(now.getDate() + 1);
      this.selectDate(tomorrow, event);
    } else {
      this.newTaskDueDate = '';
      this.showCalendar = false;
    }
  }

  isSelectedDate(date: Date): boolean {
    if (!this.newTaskDueDate) return false;
    const d = new Date(this.newTaskDueDate);
    return date.getDate() === d.getDate() && 
           date.getMonth() === d.getMonth() && 
           date.getFullYear() === d.getFullYear();
  }

  isToday(date: Date): boolean {
    const now = new Date();
    return date.getDate() === now.getDate() && 
           date.getMonth() === now.getMonth() && 
           date.getFullYear() === now.getFullYear();
  }
}
