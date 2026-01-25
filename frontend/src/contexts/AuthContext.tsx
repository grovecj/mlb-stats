import { createContext, useContext, useState, useEffect, ReactNode } from 'react';

type Role = 'USER' | 'ADMIN' | 'OWNER';

interface User {
  name: string;
  email: string;
  picture: string;
  role: Role;
}

interface AuthContextType {
  user: User | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  isAdmin: boolean;
  isOwner: boolean;
  hasRole: (role: Role) => boolean;
  login: () => void;
  logout: () => void;
}

const ROLE_HIERARCHY: Record<Role, number> = {
  USER: 0,
  ADMIN: 1,
  OWNER: 2,
};

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    checkAuthStatus();
  }, []);

  const checkAuthStatus = async () => {
    try {
      const response = await fetch('/api/auth/me', {
        credentials: 'include',
      });

      if (response.status === 401 || response.status === 403) {
        setUser(null);
        setIsLoading(false);
        return;
      }

      const data = await response.json();

      if (data.authenticated) {
        setUser({
          name: data.name,
          email: data.email,
          picture: data.picture,
          role: data.role || 'USER',
        });
      } else {
        setUser(null);
      }
    } catch (error) {
      console.error('Auth check failed:', error);
      setUser(null);
    } finally {
      setIsLoading(false);
    }
  };

  const login = () => {
    window.location.href = '/oauth2/authorization/google';
  };

  const logout = async () => {
    try {
      const response = await fetch('/api/auth/logout', {
        method: 'POST',
        credentials: 'include',
      });
      if (response.redirected) {
        window.location.href = response.url;
        return;
      }
    } catch (error) {
      console.error('Logout failed:', error);
    }
    setUser(null);
    window.location.href = '/';
  };

  const hasRole = (role: Role): boolean => {
    if (!user) return false;
    return ROLE_HIERARCHY[user.role] >= ROLE_HIERARCHY[role];
  };

  const isAdmin = user ? hasRole('ADMIN') : false;
  const isOwner = user ? user.role === 'OWNER' : false;

  return (
    <AuthContext.Provider
      value={{
        user,
        isAuthenticated: !!user,
        isLoading,
        isAdmin,
        isOwner,
        hasRole,
        login,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}
