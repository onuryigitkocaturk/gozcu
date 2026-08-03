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
  listMine: () => api.get<ProjectResponse[]>("/api/projects/my"),
  create: (body: ProjectRequest) => api.post<ProjectResponse>("/api/projects", body),
  remove: (id: string) => api.del<void>(`/api/projects/${id}`),
  addUser: (projectId: string, userId: string) => api.post<void>(`/api/projects/${projectId}/users/${userId}`),
  removeUser: (projectId: string, userId: string) =>
    api.del<void>(`/api/projects/${projectId}/users/${userId}`),
  listUsers: (projectId: string) => api.get<UserResponse[]>(`/api/projects/${projectId}/users`),

  addTable: (projectId: string, body: ProjectTableRequest) =>
    api.post<void>(`/api/projects/${projectId}/tables`, body),
  listTables: (projectId: string) => api.get<ProjectTableResponse[]>(`/api/projects/${projectId}/tables`),
  getTableData: (projectId: string, tableName: string) =>
    api.get<TableRow[]>(`/api/projects/${projectId}/tables/${encodeURIComponent(tableName)}/data`),
  discoverTables: (projectId: string) => api.get<string[]>(`/api/projects/${projectId}/discover-tables`),
  getTableColumns: (projectId: string, tableName: string) =>
    api.get<string[]>(`/api/projects/${projectId}/tables/${encodeURIComponent(tableName)}/columns`),
};
