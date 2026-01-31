import { useState, useEffect } from 'react';
import {
  addWeeks,
  subWeeks,
  addMonths,
  subMonths,
  format,
  startOfWeek,
  endOfWeek,
} from 'date-fns';
import { Game } from '../types/game';
import { Team } from '../types/team';
import { getGames, getTeams } from '../services/api';
import GameCard from '../components/game/GameCard';
import WeekView from '../components/schedule/WeekView';
import MonthView from '../components/schedule/MonthView';
import './GamesPage.css';

type ViewMode = 'day' | 'week' | 'month';

function GamesPage() {
  const [games, setGames] = useState<Game[]>([]);
  const [teams, setTeams] = useState<Team[]>([]);
  const [currentDate, setCurrentDate] = useState(() => new Date());
  const [viewMode, setViewMode] = useState<ViewMode>('week');
  const [teamFilter, setTeamFilter] = useState<number | undefined>(undefined);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Load teams for filter dropdown
  useEffect(() => {
    async function fetchTeams() {
      try {
        const data = await getTeams();
        setTeams(data.sort((a, b) => a.name.localeCompare(b.name)));
      } catch (_err) {
        // Teams are optional for filtering, don't show error
      }
    }
    fetchTeams();
  }, []);

  // Load games for day view
  useEffect(() => {
    if (viewMode !== 'day') return;

    async function fetchGames() {
      setLoading(true);
      setError(null);
      try {
        const dateStr = format(currentDate, 'yyyy-MM-dd');
        const data = await getGames({ date: dateStr, teamId: teamFilter });
        setGames(Array.isArray(data) ? data : (data as { content: Game[] }).content || []);
      } catch (_err) {
        setError('Failed to load games');
        setGames([]);
      } finally {
        setLoading(false);
      }
    }
    fetchGames();
  }, [currentDate, teamFilter, viewMode]);

  const navigateDate = (direction: 'prev' | 'next') => {
    if (viewMode === 'day') {
      setCurrentDate((prev) => {
        const newDate = new Date(prev);
        newDate.setDate(newDate.getDate() + (direction === 'next' ? 1 : -1));
        return newDate;
      });
    } else if (viewMode === 'week') {
      setCurrentDate((prev) =>
        direction === 'next' ? addWeeks(prev, 1) : subWeeks(prev, 1)
      );
    } else {
      setCurrentDate((prev) =>
        direction === 'next' ? addMonths(prev, 1) : subMonths(prev, 1)
      );
    }
  };

  const goToToday = () => {
    setCurrentDate(new Date());
  };

  const handleDaySelect = (date: Date) => {
    setCurrentDate(date);
    setViewMode('day');
  };

  const formatDateDisplay = (): string => {
    if (viewMode === 'day') {
      return format(currentDate, 'EEEE, MMMM d, yyyy');
    } else if (viewMode === 'week') {
      const weekStart = startOfWeek(currentDate, { weekStartsOn: 0 });
      const weekEnd = endOfWeek(currentDate, { weekStartsOn: 0 });
      return `${format(weekStart, 'MMM d')} - ${format(weekEnd, 'MMM d, yyyy')}`;
    }
    return format(currentDate, 'MMMM yyyy');
  };

  const getNavLabel = (direction: 'prev' | 'next'): string => {
    const prefix = direction === 'prev' ? 'Prev' : 'Next';
    if (viewMode === 'day') return prefix;
    if (viewMode === 'week') return `${prefix} Week`;
    return `${prefix} Month`;
  };

  return (
    <div className="games-page">
      <h1 className="page-title">MLB Schedule</h1>

      <div className="schedule-controls">
        {/* View Toggle */}
        <div className="view-toggle">
          <button
            className={`view-toggle-btn ${viewMode === 'day' ? 'active' : ''}`}
            onClick={() => setViewMode('day')}
          >
            Day
          </button>
          <button
            className={`view-toggle-btn ${viewMode === 'week' ? 'active' : ''}`}
            onClick={() => setViewMode('week')}
          >
            Week
          </button>
          <button
            className={`view-toggle-btn ${viewMode === 'month' ? 'active' : ''}`}
            onClick={() => setViewMode('month')}
          >
            Month
          </button>
        </div>

        {/* Navigation */}
        <div className="schedule-nav">
          <button className="nav-btn" onClick={() => navigateDate('prev')}>
            &larr; {getNavLabel('prev')}
          </button>
          <span className="schedule-date">{formatDateDisplay()}</span>
          <button className="nav-btn" onClick={() => navigateDate('next')}>
            {getNavLabel('next')} &rarr;
          </button>
          <button className="today-btn" onClick={goToToday}>
            Today
          </button>
        </div>

        {/* Team Filter */}
        <div className="team-filter">
          <select
            value={teamFilter || ''}
            onChange={(e) =>
              setTeamFilter(e.target.value ? Number(e.target.value) : undefined)
            }
            className="team-filter-select"
          >
            <option value="">All Teams</option>
            {teams.map((team) => (
              <option key={team.id} value={team.id}>
                {team.name}
              </option>
            ))}
          </select>
        </div>
      </div>

      {/* View Content */}
      {viewMode === 'month' ? (
        <MonthView
          currentDate={currentDate}
          teamId={teamFilter}
          onDaySelect={handleDaySelect}
        />
      ) : viewMode === 'week' ? (
        <WeekView
          currentDate={currentDate}
          teamId={teamFilter}
          onDaySelect={handleDaySelect}
        />
      ) : loading ? (
        <div className="loading">Loading games...</div>
      ) : error ? (
        <div className="error">{error}</div>
      ) : games.length > 0 ? (
        <div className="grid grid-3">
          {games.map((game) => (
            <GameCard key={game.id} game={game} />
          ))}
        </div>
      ) : (
        <p className="no-games">No games scheduled for this date.</p>
      )}
    </div>
  );
}

export default GamesPage;
