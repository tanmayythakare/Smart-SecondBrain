import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { TaskListComponent } from './features/tasks/task-list/task-list.component';
import { AuthGuard } from './core/guards/auth.guard';
import { NoteListComponent } from './features/notes/note-list/note-list.component';
import { NoteGraphComponent } from './features/notes/note-graph/note-graph.component';

const routes: Routes = [
  {
    path: 'login',
    loadChildren: () =>
      import('./features/auth/auth.module').then(m => m.AuthModule)
  },
  {
    path: 'tasks',
    component: TaskListComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'graph',
    component: NoteGraphComponent,
    canActivate: [AuthGuard]
  },
  {
  path: 'notes/:id',
  component: NoteListComponent,
  canActivate: [AuthGuard]
  },
   {
    path: 'notes',
    component: NoteListComponent,
    canActivate: [AuthGuard]
  },
  {
    path: '',
    redirectTo: 'login',
    pathMatch: 'full'
  },
  {
    path: '**',
    redirectTo: 'login'
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
