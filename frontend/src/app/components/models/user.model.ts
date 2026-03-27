export interface UserModel {
  id: number;
  email: string;
  profile?: {
    id: number;
    profilePhoto?: string;
    firstName?: string;
    lastName?: string;
  };
}
