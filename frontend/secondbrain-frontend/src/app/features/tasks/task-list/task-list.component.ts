import { Component, OnInit } from '@angular/core';
import { TaskService } from '../task.service';

// Task interface remains unchanged
export interface Task {
  id?: number;
  title: string;
}

@Component({
  selector: 'app-task-list',
  templateUrl: './task-list.component.html'
})
export class TaskListComponent implements OnInit {

  tasks: Task[] = [];
  newTaskTitle = '';

  // Edit mode properties
  editingTaskId: number | null = null;
  editingTaskTitle = '';

  // Delete confirmation
  deleteConfirmTaskId: number | null = null;

  // ✅ NEW: loading & error states
  isLoading = false;
  errorMessage: string | null = null;

  constructor(private taskService: TaskService) {}

  ngOnInit(): void {
    this.loadTasks();
  }

  loadTasks() {
    this.isLoading = true;          // NEW
    this.errorMessage = null;       // NEW

    this.taskService.getTasks().subscribe({
      next: (tasks) => {
        this.tasks = tasks;
        this.isLoading = false;     // NEW
      },
      error: (error) => {
        console.error(error);
        this.errorMessage = 'Failed to load tasks. Please try again.';
        this.isLoading = false;     // NEW
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

    this.isLoading = true;          // NEW
    this.errorMessage = null;       // NEW

    this.taskService.createTask(this.newTaskTitle).subscribe({
      next: () => {
        this.newTaskTitle = '';
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

  saveEdit(task: Task) {
    if (!this.editingTaskTitle.trim()) {
      this.cancelEdit();
      return;
    }

    this.isLoading = true;          // NEW
    this.errorMessage = null;       // NEW

    this.taskService.updateTask(task.id!, this.editingTaskTitle).subscribe({
      next: () => {
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

  confirmDelete(task: Task) {
    this.isLoading = true;          // NEW
    this.errorMessage = null;       // NEW

    this.taskService.deleteTask(task.id!).subscribe({
      next: () => {
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
}
