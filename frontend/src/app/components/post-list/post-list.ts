import {Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {PostService} from '../services/post.service';
import {OwnerType, Post} from '../models/post.model';
import {CommonModule, DatePipe, NgForOf} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {PostCommentsComponent} from '../post-comments/post-comments';

@Component({
  selector: 'app-post-list',
  templateUrl: './post-list.html',
  imports: [
    DatePipe,
    FormsModule,
    CommonModule,
    PostCommentsComponent,
    NgForOf
  ],
  styleUrls: ['./post-list.css']
})
export class PostListComponent implements OnChanges {
  @Input() ownerId!: number;
  @Input() ownerType!: OwnerType;
  @Input() canPost: boolean = false;

  posts: Post[] = [];
  newPostContent: string = '';

  constructor(private postService: PostService) {}

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['ownerId'] && changes['ownerId'].currentValue) {
      this.loadPosts();
    }
  }

  loadPosts(): void {
    console.log("PostListComponent: Loading posts for ownerId", this.ownerId, "and ownerType", this.ownerType);
    this.postService.getPosts(this.ownerId, this.ownerType).subscribe(data => {
      this.posts = data;
    });
  }

  submitPost(): void {
    const postData = {
      authorId: parseInt(localStorage.getItem('userId') || '0', 10),
      ownerId: this.ownerId,
      ownerType: this.ownerType,
      content: this.newPostContent
    };

    this.postService.createPost(postData).subscribe(savedPost => {
      this.posts.unshift(savedPost);
      this.newPostContent = '';
    });
  }
}
