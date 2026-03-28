export interface ChatSummaryDTO {
  chatId: string;
  interlocutorId: number;
  interlocutorName: string;
  lastMessage: string;
  lastMessageTime: string | Date;
  unreadCount: number;
  avatarUrl?: string;
}
