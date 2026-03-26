export interface CommentModel {
  id?: string;
  postId: string;
  authorId: number;
  authorName: string;
  text: string;
  createDate: string;
}
