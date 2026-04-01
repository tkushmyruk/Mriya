import { Component, OnInit, signal } from '@angular/core';
import { FeedService } from '../services/feed.service';
import { Post } from '../models/post.model';
import {PostService} from '../services/post.service';
import {PostListComponent} from '../post-list/post-list';
import {CommonModule} from '@angular/common';

@Component({
  selector: 'app-feed',
  templateUrl: 'feed.html',
  imports: [PostListComponent, CommonModule]
})
export class FeedComponent implements OnInit {
  smartPosts = signal<Post[]>([]);

  constructor(private feedService: FeedService) {}

  ngOnInit() {
    this.feedService.getSmartFeed().subscribe(posts => {
      this.smartPosts.set(posts);
    });
  }
}
