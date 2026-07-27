import { api } from "./client";
import type { ChangeRoleRequest, UpdateUserRequest, UserResponse } from "../types/api";

export const usersApi = {
  me: () => api.get<UserResponse>("/api/users/me"),
  list: () => api.get<UserResponse[]>("/api/users"),
  update: (id: number, body: UpdateUserRequest) => api.put<UserResponse>(`/api/users/${id}`, body),
  remove: (id: number) => api.del<void>(`/api/users/${id}`),
  changeRole: (id: number, body: ChangeRoleRequest) => api.put<UserResponse>(`/api/users/${id}/role`, body),
};
