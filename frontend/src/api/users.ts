import { api } from "./client";
import type { ChangeRoleRequest, MyAccountResponse, UpdateUserRequest, UserResponse } from "../types/api";

export const usersApi = {
  me: () => api.get<UserResponse>("/api/users/me"),
  myAccount: () => api.get<MyAccountResponse>("/api/users/me/account"),
  list: () => api.get<UserResponse[]>("/api/users"),
  update: (id: string, body: UpdateUserRequest) => api.put<UserResponse>(`/api/users/${id}`, body),
  remove: (id: string) => api.del<void>(`/api/users/${id}`),
  changeRole: (id: string, body: ChangeRoleRequest) => api.put<UserResponse>(`/api/users/${id}/role`, body),
};
