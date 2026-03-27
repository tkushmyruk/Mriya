import { Component } from '@angular/core';
import { Subject, debounceTime, distinctUntilChanged, switchMap, of } from 'rxjs';
import { ProfileModel } from '../../profile/model/profile-model';
import { ProfileService } from '../../profile/service/profile-service';
import { RouterLink } from '@angular/router';
import { NgForOf, NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-search-component',
  standalone: true,
  imports: [
    RouterLink,
    NgForOf,
    NgIf,
    FormsModule
  ],
  templateUrl: './search-component.html',
  styleUrl: './search-component.css',
})
export class SearchComponent {
  public searchQuery: string = '';
  public searchResults: ProfileModel[] = [];
  private searchSubject = new Subject<string>();

  constructor(private profileService: ProfileService) {
    this.searchSubject.pipe(
      debounceTime(400),
      distinctUntilChanged(),
      switchMap(query => {
        if (query.trim().length < 2) return of([]);
        return this.profileService.search(query);
      })
    ).subscribe(results => {
      this.searchResults = results;
    });
  }

  onSearchChange() {
    this.searchSubject.next(this.searchQuery);
  }
}
