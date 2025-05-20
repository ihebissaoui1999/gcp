export interface Notification {
    chatId?: string;
    content?: string;
    senderId?: string;
    receiverId?: string;
    messageType?: 'TEXT' | 'IMAGE' | 'VIDEO' | 'AUDIO';
    type?: 'SEEN' | 'MESSAGE' | 'IMAGE' | 'VIDEO' | 'AUDIO';
    notificationStatus?: 'ADDED'|'ACCEPTED'| 'REJECTED'|'COMPLETED';
    chatName?: string;
    media?: Array<string>;
  }