import { api } from "./client";
import type { GroupRequest, GroupResponse, UserResponse } from "../types/api";

export const groupsApi = {
  list: () => api.get<GroupResponse[]>("/api/groups"),
  create: (body: GroupRequest) => api.post<GroupResponse>("/api/groups", body),
  remove: (id: string) => api.del<void>(`/api/groups/${id}`),
  addUser: (groupId: string, userId: string) => api.post<void>(`/api/groups/${groupId}/users/${userId}`),
  removeUser: (groupId: string, userId: string) => api.del<void>(`/api/groups/${groupId}/users/${userId}`),
  listUsers: (groupId: string) => api.get<UserResponse[]>(`/api/groups/${groupId}/users`),
};
