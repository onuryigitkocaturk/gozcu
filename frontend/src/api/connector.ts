import { api } from "./client";

export const connectorApi = {
  listTables: () => api.get<string[]>("/api/connector/tables"),
  listColumns: (tableName: string) =>
    api.get<string[]>(`/api/connector/tables/${encodeURIComponent(tableName)}/columns`),
};
