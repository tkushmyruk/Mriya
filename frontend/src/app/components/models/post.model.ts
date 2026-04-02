export type OwnerType = 'PROFILE' | 'GROUP' | 'PAGE';

export interface Post {
  id?: string;
  authorId: number;
  authorFirstName?: string; 
  authorLastName?: string;
  ownerId: number;
  ownerType: OwnerType;
  content: string;
  likesCount: number;
  commentsCount: number;
  showComments?: boolean;
  createdAt: Date;
}
