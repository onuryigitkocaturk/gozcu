import { api } from "./client";
import type {
  MyProjectResponse,
  ProjectDashboardStatsResponse,
  ProjectMemberResponse,
  ProjectMembershipRequest,
  ProjectRequest,
  ProjectResponse,
  ProjectTableRequest,
  ProjectTableResponse,
  TableRow,
} from "../types/api";

export const projectsApi = {
  list: () => api.get<ProjectResponse[]>("/api/projects"),
  listMine: () => api.get<MyProjectResponse[]>("/api/projects/my"),
  getDashboardStats: (projectId: string) =>
    api.get<ProjectDashboardStatsResponse>(`/api/projects/${projectId}/dashboard-stats`),
  getById: (id: string) => api.get<ProjectResponse>(`/api/projects/${id}`),
  create: (body: ProjectRequest) => api.post<ProjectResponse>("/api/projects", body),
  remove: (id: string) => api.del<void>(`/api/projects/${id}`),
  addUser: (projectId: string, userId: string, body: ProjectMembershipRequest) =>
    api.post<void>(`/api/projects/${projectId}/users/${userId}`, body),
  changeMemberRole: (projectId: string, userId: string, body: ProjectMembershipRequest) =>
    api.put<void>(`/api/projects/${projectId}/users/${userId}/role`, body),
  removeUser: (projectId: string, userId: string) =>
    api.del<void>(`/api/projects/${projectId}/users/${userId}`),
  listUsers: (projectId: string) => api.get<ProjectMemberResponse[]>(`/api/projects/${projectId}/users`),

  addTable: (projectId: string, body: ProjectTableRequest) =>
    api.post<void>(`/api/projects/${projectId}/tables`, body),
  listTables: (projectId: string) => api.get<ProjectTableResponse[]>(`/api/projects/${projectId}/tables`),
  getTableData: (projectId: string, tableName: string) =>
    api.get<TableRow[]>(`/api/projects/${projectId}/tables/${encodeURIComponent(tableName)}/data`),
  discoverTables: (projectId: string) => api.get<string[]>(`/api/projects/${projectId}/discover-tables`),
  getTableColumns: (projectId: string, tableName: string) =>
    api.get<string[]>(`/api/projects/${projectId}/tables/${encodeURIComponent(tableName)}/columns`),
};
