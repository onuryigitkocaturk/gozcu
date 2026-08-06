import { useAuth } from "../context/AuthContext";
import { useAsync } from "./useAsync";
import { projectsApi } from "../api/projects";
import type { ProjectRole } from "../types/api";

const ROLE_RANK: Record<ProjectRole, number> = {
  REPORTER: 0,
  DEVELOPER: 1,
  MAINTAINER: 2,
  OWNER: 3,
};

/**
 * Giris yapan kullanicinin BELIRLI bir projedeki rolunu bulur (global
 * isAdmin'den bagimsiz) - butonlari "en az su rol" mantigiyla gizlemek/
 * gostermek icin kullanilir. Global ADMIN her zaman her seyi yapabilir.
 */
export function useMyProjectRole(projectId: string) {
  const { user, isAdmin } = useAuth();
  const { data: members, loading } = useAsync(() => projectsApi.listUsers(projectId), [projectId]);

  const myRole = members?.find((m) => m.userId === user?.id)?.role ?? null;

  const isAtLeast = (minRole: ProjectRole) =>
    isAdmin || (myRole !== null && ROLE_RANK[myRole] >= ROLE_RANK[minRole]);

  return {
    role: myRole,
    loading,
    isAtLeastDeveloper: isAtLeast("DEVELOPER"),
    isAtLeastMaintainer: isAtLeast("MAINTAINER"),
    isOwner: isAtLeast("OWNER"),
  };
}
