import { api } from "./client";
import type {
  ProjectRequest,
  ProjectResponse,
  ProjectTableRequest,
  ProjectTableResponse,
  TableRow,
  UserResponse,
} from "../types/api";

export const projectsApi = {
  list: () => api.get<ProjectResponse[]>("/api/projects"),
  create: (body: ProjectRequest) => api.post<ProjectResponse>("/api/projects", body),
  remove: (id: number) => api.del<void>(`/api/projects/${id}`),
  addUser: (projectId: number, userId: number) => api.post<void>(`/api/projects/${projectId}/users/${userId}`),
  removeUser: (projectId: number, userId: number) =>
    api.del<void>(`/api/projects/${projectId}/users/${userId}`),
  listUsers: (projectId: number) => api.get<UserResponse[]>(`/api/projects/${projectId}/users`),

  addTable: (projectId: number, body: ProjectTableRequest) =>
    api.post<void>(`/api/projects/${projectId}/tables`, body),
  listTables: (projectId: number) => api.get<ProjectTableResponse[]>(`/api/projects/${projectId}/tables`),
  getTableData: (projectId: number, tableName: string) =>
    api.get<TableRow[]>(`/api/projects/${projectId}/tables/${encodeURIComponent(tableName)}/data`),
};
