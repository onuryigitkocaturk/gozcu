const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080";

export class ApiError extends Error {
  status: number;

  constructor(status: number, message: string) {
    super(message);
    this.status = status;
    this.name = "ApiError";
  }
}

let currentToken: string | null = null;

/** Token sadece bellekte tutulur (localStorage KULLANILMAZ - bilincli tercih). */
export function setAuthToken(token: string | null) {
  currentToken = token;
}

async function parseErrorMessage(response: Response): Promise<string> {
  try {
    const data = await response.json();
    if (typeof data?.message === "string") {
      return data.message;
    }
  } catch {
    // body JSON degildi (orn. Spring Security'nin varsayilan 401/403 sayfasi)
  }

  switch (response.status) {
    case 401:
      return "Oturum süresi doldu veya giriş yapılmadı.";
    case 403:
      return "Bu işlem için yetkin yok.";
    case 404:
      return "Kayıt bulunamadı.";
    default:
      return `İstek başarısız oldu (${response.status}).`;
  }
}

interface RequestOptions {
  method?: "GET" | "POST" | "PUT" | "DELETE";
  body?: unknown;
}

async function request<T>(path: string, options: RequestOptions = {}): Promise<T> {
  const headers: Record<string, string> = {};
  if (options.body !== undefined) {
    headers["Content-Type"] = "application/json";
  }
  if (currentToken) {
    headers["Authorization"] = `Bearer ${currentToken}`;
  }

  const response = await fetch(`${API_BASE_URL}${path}`, {
    method: options.method ?? "GET",
    headers,
    // device_token (yeni cihaz dogrulama) cookie'sinin gonderilip/alinabilmesi icin gerekli.
    credentials: "include",
    body: options.body !== undefined ? JSON.stringify(options.body) : undefined,
  });

  if (!response.ok) {
    throw new ApiError(response.status, await parseErrorMessage(response));
  }

  if (response.status === 204) {
    return undefined as T;
  }

  const text = await response.text();
  return (text ? JSON.parse(text) : undefined) as T;
}

export const api = {
  get: <T>(path: string) => request<T>(path, { method: "GET" }),
  post: <T>(path: string, body?: unknown) => request<T>(path, { method: "POST", body }),
  put: <T>(path: string, body?: unknown) => request<T>(path, { method: "PUT", body }),
  del: <T>(path: string) => request<T>(path, { method: "DELETE" }),
};
