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
  @Input() customPosts: Post[] | null = null;
  @Input() isSmartFeed: boolean = false;

  posts: Post[] = [];
  newPostContent: string = '';
  isExpanded = false;

  constructor(private postService: PostService) {}

  ngOnChanges(changes: SimpleChanges): void {
    if (this.customPosts) {
      this.posts = this.customPosts;
      return;
    }
    if (changes['ownerId'] && changes['ownerId'].currentValue) {
      this.loadPosts();
    }
  }

  loadPosts(): void {
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
      this.isExpanded = false;
      this.newPostContent = '';
    });
  }

  isLiked(post: any): boolean {
    const currentUserId = parseInt(localStorage.getItem('userId') || '0', 10);
    return post.likedBy?.includes(currentUserId);
  }

  likePost(post: any) {
    this.postService.toggleLike(post.id).subscribe({
      next: (updatedPost) => {
        Object.assign(post, updatedPost);
      },
      error: (err) => {
        console.error('Помилка при лайку:', err);
      }
    });
  }

  toggleExpand(state: boolean) {
    this.isExpanded = state;
  }
}
