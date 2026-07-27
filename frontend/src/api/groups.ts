import { api } from "./client";
import type { GroupRequest, GroupResponse, UserResponse } from "../types/api";

export const groupsApi = {
  list: () => api.get<GroupResponse[]>("/api/groups"),
  create: (body: GroupRequest) => api.post<GroupResponse>("/api/groups", body),
  remove: (id: number) => api.del<void>(`/api/groups/${id}`),
  addUser: (groupId: number, userId: number) => api.post<void>(`/api/groups/${groupId}/users/${userId}`),
  removeUser: (groupId: number, userId: number) => api.del<void>(`/api/groups/${groupId}/users/${userId}`),
  listUsers: (groupId: number) => api.get<UserResponse[]>(`/api/groups/${groupId}/users`),
};
