export interface UserModel {
  id: number;
  username: string;
  email: string
}

export interface UpdateUserModel {
  username: string;
  email: string;
  password: string | null;
}
