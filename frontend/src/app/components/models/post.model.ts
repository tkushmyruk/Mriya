export type OwnerType = 'PROFILE' | 'GROUP' | 'PAGE';

export interface Post {
  id?: string;
  authorId: number;
  ownerId: number;
  ownerType: OwnerType;
  content: string;
  likesCount: number;
  commentsCount: number;
  createdAt: Date;
}
