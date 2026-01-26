import { useState, useEffect, useCallback } from 'react';
import {
  isTeamFavorite,
  addTeamFavorite,
  removeTeamFavorite,
  isPlayerFavorite,
  addPlayerFavorite,
  removePlayerFavorite,
} from '../services/api';

export function useTeamFavorite(teamId: number | undefined) {
  const [isFavorite, setIsFavorite] = useState(false);
  const [loading, setLoading] = useState(true);
  const [toggling, setToggling] = useState(false);

  useEffect(() => {
    if (!teamId) {
      setLoading(false);
      return;
    }

    let cancelled = false;
    const id = teamId;

    async function checkFavorite() {
      try {
        const result = await isTeamFavorite(id);
        if (!cancelled) {
          setIsFavorite(result);
        }
      } catch (_err) {
        // User might not be authenticated, silently fail
        if (!cancelled) {
          setIsFavorite(false);
        }
      } finally {
        if (!cancelled) {
          setLoading(false);
        }
      }
    }

    checkFavorite();
    return () => {
      cancelled = true;
    };
  }, [teamId]);

  const toggleFavorite = useCallback(async () => {
    if (!teamId || toggling) return;

    setToggling(true);
    try {
      if (isFavorite) {
        await removeTeamFavorite(teamId);
        setIsFavorite(false);
      } else {
        await addTeamFavorite(teamId);
        setIsFavorite(true);
      }
    } catch (err) {
      console.error('Failed to toggle team favorite:', err);
    } finally {
      setToggling(false);
    }
  }, [teamId, isFavorite, toggling]);

  return { isFavorite, loading, toggling, toggleFavorite };
}

export function usePlayerFavorite(playerId: number | undefined) {
  const [isFavorite, setIsFavorite] = useState(false);
  const [loading, setLoading] = useState(true);
  const [toggling, setToggling] = useState(false);

  useEffect(() => {
    if (!playerId) {
      setLoading(false);
      return;
    }

    let cancelled = false;
    const id = playerId;

    async function checkFavorite() {
      try {
        const result = await isPlayerFavorite(id);
        if (!cancelled) {
          setIsFavorite(result);
        }
      } catch (_err) {
        // User might not be authenticated, silently fail
        if (!cancelled) {
          setIsFavorite(false);
        }
      } finally {
        if (!cancelled) {
          setLoading(false);
        }
      }
    }

    checkFavorite();
    return () => {
      cancelled = true;
    };
  }, [playerId]);

  const toggleFavorite = useCallback(async () => {
    if (!playerId || toggling) return;

    setToggling(true);
    try {
      if (isFavorite) {
        await removePlayerFavorite(playerId);
        setIsFavorite(false);
      } else {
        await addPlayerFavorite(playerId);
        setIsFavorite(true);
      }
    } catch (err) {
      console.error('Failed to toggle player favorite:', err);
    } finally {
      setToggling(false);
    }
  }, [playerId, isFavorite, toggling]);

  return { isFavorite, loading, toggling, toggleFavorite };
}
