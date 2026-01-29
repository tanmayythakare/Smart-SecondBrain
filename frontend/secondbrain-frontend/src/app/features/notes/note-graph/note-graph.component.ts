import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import ForceGraph from 'force-graph';
import { NoteService } from '../note.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-note-graph',
  templateUrl: './note-graph.component.html'
})
export class NoteGraphComponent implements OnInit {

  @ViewChild('graphContainer', { static: true })
  graphContainer!: ElementRef<HTMLDivElement>;

  graph: any;
  highlightedIds = new Set<number>();

  constructor(
    private noteService: NoteService,
    private router: Router
  ) { }



  highlightNotes(ids: number[]) {
    this.highlightedIds = new Set(ids.map(Number));
    this.graph.refresh();
  }

  resetView() {
    if (!this.graph) return;

    setTimeout(() => {
      this.graph
        .width(this.graphContainer.nativeElement.offsetWidth)
        .height(this.graphContainer.nativeElement.offsetHeight);

      this.graph.zoomToFit(400, 120);
    }, 0);
  }




  ngOnInit(): void {

    this.graph = new ForceGraph(this.graphContainer.nativeElement)
      .nodeLabel('name')
      .nodeRelSize(3)
      .linkDirectionalArrowLength(4)
      .linkDirectionalArrowRelPos(1)
      .nodeColor((node: any) =>
        this.highlightedIds.has(Number(node.id))
          ? 'orange'
          : '#1f77b4'
      )
      .onNodeClick((node: any) => {
        this.graph.centerAt(node.x, node.y, 800);
        this.graph.zoom(4, 800);
        this.router.navigate(['/notes', node.id]);
      });


    this.noteService.getNotes().subscribe((notes: any[]) => {

      const nodes = notes.map(note => ({
        id: note.id,
        name: note.title
      }));

      const links: any[] = [];
      let pending = notes.length;

      if (pending === 0) {
        this.graph.graphData({ nodes, links });
      }


      notes.forEach(note => {
        this.noteService.getRelatedNotes(note.id)
          .subscribe((related: any[]) => {

            related.forEach(rel => {
              links.push({
                source: note.id,
                target: rel.id
              });
            });

            pending--;

            if (pending === 0) {
              this.graph.graphData({ nodes, links });

              setTimeout(() => {
                this.graph
                  .width(this.graphContainer.nativeElement.offsetWidth)
                  .height(this.graphContainer.nativeElement.offsetHeight);

                this.graph.zoomToFit(400, 120);
              }, 50);
            }

          });
      });
    });
  }
}
