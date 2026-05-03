import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { TaskListComponent } from './features/tasks/task-list/task-list.component';
import { AuthGuard } from './core/guards/auth.guard';
import { NoteListComponent } from './features/notes/note-list/note-list.component';
import { NoteDetailComponent } from './features/notes/note-detail/note-detail.component';
import { AiChatComponent } from './features/ai-chat/ai-chat.component';

const routes: Routes = [
  {
    path: 'auth',
    loadChildren: () =>
      import('./features/auth/auth.module').then(m => m.AuthModule)
  },
  {
    path: 'tasks',
    component: TaskListComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'ai',
    component: AiChatComponent,
    canActivate: [AuthGuard]
  },

  {
    path: 'notes/:id',
    component: NoteDetailComponent,
    canActivate: [AuthGuard]
  },
   {
    path: 'notes',
    component: NoteListComponent,
    canActivate: [AuthGuard]
  },
  {
    path: '',
    redirectTo: 'auth/login',
    pathMatch: 'full'
  },
  {
    path: '**',
    redirectTo: 'auth/login'
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
