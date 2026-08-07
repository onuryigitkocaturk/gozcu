import { createContext, useCallback, useContext, useMemo, useState, type ReactNode } from "react";
import { authApi } from "../api/auth";
import { usersApi } from "../api/users";
import { setAuthToken } from "../api/client";
import type { LoginRequest, RegisterRequest, UserResponse } from "../types/api";

interface AuthContextValue {
  user: UserResponse | null;
  isAdmin: boolean;
  isAuthenticated: boolean;
  login: (body: LoginRequest) => Promise<{ verificationRequired: boolean }>;
  verifyLoginCode: (code: string) => Promise<void>;
  register: (body: RegisterRequest) => Promise<void>;
  logout: () => void;
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

// Token ve kullanici bilgisi SADECE bellekte (React state) tutulur.
// localStorage/sessionStorage KULLANILMAZ - sayfa yenilenince oturum
// bilerek kapanir (backend'in "access token localStorage'da saklanmaz"
// kuraliyla tutarli).
export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<UserResponse | null>(null);
  // Bilinmeyen bir cihazdan giris yapildiginda, kod dogrulanana kadar bu
  // bekliyor - JWT henuz yok, sadece "hangi giris denemesi" bilgisi var.
  const [pendingVerificationToken, setPendingVerificationToken] = useState<string | null>(null);

  const completeLogin = useCallback(async (token: string) => {
    setAuthToken(token);
    try {
      const me = await usersApi.me();
      setUser(me);
    } catch (err) {
      setAuthToken(null);
      throw err;
    }
  }, []);

  const login = useCallback(
    async (body: LoginRequest) => {
      const result = await authApi.login(body);
      if (result.verificationRequired && result.verificationToken) {
        setPendingVerificationToken(result.verificationToken);
        return { verificationRequired: true };
      }
      await completeLogin(result.token as string);
      return { verificationRequired: false };
    },
    [completeLogin],
  );

  const verifyLoginCode = useCallback(
    async (code: string) => {
      if (!pendingVerificationToken) {
        throw new Error("Doğrulama isteği bulunamadı, tekrar giriş yapmayı dene.");
      }
      const result = await authApi.verifyLoginCode({ verificationToken: pendingVerificationToken, code });
      await completeLogin(result.token as string);
      setPendingVerificationToken(null);
    },
    [pendingVerificationToken, completeLogin],
  );

  const register = useCallback(async (body: RegisterRequest) => {
    await authApi.register(body);
  }, []);

  const logout = useCallback(() => {
    setAuthToken(null);
    setUser(null);
    setPendingVerificationToken(null);
  }, []);

  const value = useMemo<AuthContextValue>(
    () => ({
      user,
      isAdmin: user?.role === "ADMIN",
      isAuthenticated: user !== null,
      login,
      verifyLoginCode,
      register,
      logout,
    }),
    [user, login, verifyLoginCode, register, logout],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth(): AuthContextValue {
  const ctx = useContext(AuthContext);
  if (!ctx) {
    throw new Error("useAuth must be used within AuthProvider");
  }
  return ctx;
}
