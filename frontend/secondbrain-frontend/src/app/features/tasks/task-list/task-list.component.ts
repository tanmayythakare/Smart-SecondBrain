import { Component, OnInit } from '@angular/core';
import { TaskService, Task } from '../task.service';

@Component({
  selector: 'app-task-list',
  templateUrl: './task-list.component.html'
})
export class TaskListComponent implements OnInit {

  tasks: Task[] = [];
  newTaskTitle = '';

  constructor(private taskService: TaskService) {}

  ngOnInit(): void {
    this.loadTasks();
  }

  loadTasks() {
    this.taskService.getTasks().subscribe(tasks => {
      this.tasks = tasks;
    });
  }

  addTask() {
    if (!this.newTaskTitle.trim()) return;

    this.taskService.createTask(this.newTaskTitle)
      .subscribe(() => {
        this.newTaskTitle = '';
        this.loadTasks();
      });
  }
  editTask(task: Task) {
  const newTitle = prompt('Edit task title', task.title);
  if (!newTitle) return;

  this.taskService.updateTask(task.id, newTitle)
    .subscribe(() => this.loadTasks());
}

deleteTask(task: Task) {
  if (!confirm('Delete this task?')) return;

  this.taskService.deleteTask(task.id)
    .subscribe(() => this.loadTasks());
}
  
}
