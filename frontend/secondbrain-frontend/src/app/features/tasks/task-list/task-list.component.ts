import { Component, OnInit } from '@angular/core';
import { TaskService } from '../task.service';

// Make sure your Task interface exists
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

  constructor(private taskService: TaskService) {}

  ngOnInit(): void {
    this.loadTasks();
  }

  loadTasks() {
    this.taskService.getTasks().subscribe(tasks => {
      this.tasks = tasks;
    });
  }

  // ============================================
  // ADD TASK
  // ============================================
  
  addTask() {
    if (!this.newTaskTitle.trim()) {
      return;
    }

    this.taskService.createTask(this.newTaskTitle).subscribe(() => {
      this.newTaskTitle = '';
      this.loadTasks();
    });
  }

  // ============================================
  // EDIT TASK - INLINE, NO PROMPTS
  // ============================================
  
  startEdit(task: Task) {
    // Instead of prompt, switch to inline edit mode
    this.editingTaskId = task.id!;
    this.editingTaskTitle = task.title;
    
    // Focus the input after a brief delay
    setTimeout(() => {
      const input = document.querySelector(`input[data-task-id="${task.id}"]`) as HTMLInputElement;
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

    this.taskService.updateTask(task.id!, this.editingTaskTitle).subscribe(() => {
      this.cancelEdit();
      this.loadTasks();
    });
  }

  cancelEdit() {
    this.editingTaskId = null;
    this.editingTaskTitle = '';
  }

  // Handle Enter key to save, Escape to cancel
  onEditKeydown(event: KeyboardEvent, task: Task) {
    if (event.key === 'Enter') {
      event.preventDefault();
      this.saveEdit(task);
    } else if (event.key === 'Escape') {
      this.cancelEdit();
    }
  }

  // ============================================
  // DELETE TASK - INLINE CONFIRM, NO PROMPTS
  // ============================================
  
  toggleDeleteConfirm(task: Task) {
    if (this.deleteConfirmTaskId === task.id) {
      this.deleteConfirmTaskId = null;
    } else {
      this.deleteConfirmTaskId = task.id!;
    }
  }

  isDeleteConfirmVisible(task: Task): boolean {
    return this.deleteConfirmTaskId === task.id;
  }

  confirmDelete(task: Task) {
    this.taskService.deleteTask(task.id!).subscribe(() => {
      this.deleteConfirmTaskId = null;
      this.loadTasks();
    });
  }

  cancelDelete() {
    this.deleteConfirmTaskId = null;
  }
}