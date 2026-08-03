import { api } from "./client";
import type { ConnectionTestRequest } from "../types/api";

export const connectorApi = {
  testConnection: (body: ConnectionTestRequest) => api.post<string[]>("/api/connector/test-connection", body),
};
