import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { JwtInterceptor } from './core/interceptors/jwt.interceptor';
import { TaskListComponent } from './features/tasks/task-list/task-list.component';
import { FormsModule } from '@angular/forms';
import { NoteListComponent } from './features/notes/note-list/note-list.component';
import { NoteDetailComponent } from './features/notes/note-detail/note-detail.component';
import { AiChatComponent } from './features/ai-chat/ai-chat.component';

@NgModule({
  declarations: [
    AppComponent,
    TaskListComponent,
    NoteListComponent,
    NoteDetailComponent,
    AiChatComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule
  ],
  providers: [
    { provide: HTTP_INTERCEPTORS,
      useClass: JwtInterceptor, multi: true 
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
