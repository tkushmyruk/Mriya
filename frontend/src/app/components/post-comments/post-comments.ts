import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {Component, Input, OnInit, Output} from '@angular/core';
import {CommentModel} from '../models/comment.model';
import {CommentService} from '../services/comment.service';
import { EventEmitter } from '@angular/core'

@Component({
  selector: 'app-post-comments',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './post-comments.html',
  styleUrl: './post-comments.css'
})
export class PostCommentsComponent implements OnInit {
  @Input() postId!: string;
  @Output() commentAdded = new EventEmitter<void>();
  comments: CommentModel[] = [];
  newCommentText: string = '';

  constructor(private commentService: CommentService) {}

  ngOnInit() {
    this.loadComments();
  }

  loadComments() {
    this.commentService.getComments(this.postId).subscribe(data => {
      this.comments = data;
    });
  }

  submitComment() {
    if (!this.newCommentText.trim()) return;

    this.commentService.addComment(this.postId, this.newCommentText).subscribe(newComment => {
      this.comments.push(newComment);
      this.newCommentText = '';
      this.commentAdded.emit();
    });
  }
}
