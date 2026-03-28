export interface MessageDTO {
  id?: string;
  senderId: number;
  recipientId: number;
  content: string;
  sentAt?: string | Date;
  status?: string;
}
