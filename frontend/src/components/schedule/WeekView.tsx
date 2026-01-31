import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import {
  startOfWeek,
  endOfWeek,
  addDays,
  format,
  isSameDay,
  isToday,
} from 'date-fns';
import { CalendarGame } from '../../types/game';
import { getCalendarGames } from '../../services/api';
import './WeekView.css';

interface WeekViewProps {
  currentDate: Date;
  teamId?: number;
  onDaySelect?: (date: Date) => void;
}

function WeekView({ currentDate, teamId, onDaySelect }: WeekViewProps) {
  const [games, setGames] = useState<CalendarGame[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Get week boundaries (Sunday to Saturday)
  const weekStart = startOfWeek(currentDate, { weekStartsOn: 0 });
  const weekEnd = endOfWeek(currentDate, { weekStartsOn: 0 });

  // Format dates for API and dependency tracking
  const startDateStr = format(weekStart, 'yyyy-MM-dd');
  const endDateStr = format(weekEnd, 'yyyy-MM-dd');

  // Generate array of 7 days
  const weekDays = Array.from({ length: 7 }, (_, i) => addDays(weekStart, i));

  useEffect(() => {
    async function fetchWeekGames() {
      setLoading(true);
      setError(null);
      try {
        const data = await getCalendarGames({
          startDate: startDateStr,
          endDate: endDateStr,
          teamId,
        });
        setGames(data);
      } catch (_err) {
        setError('Failed to load games');
        setGames([]);
      } finally {
        setLoading(false);
      }
    }
    fetchWeekGames();
  }, [startDateStr, endDateStr, teamId]);

  const getGamesForDay = (day: Date): CalendarGame[] => {
    const dayStr = format(day, 'yyyy-MM-dd');
    return games.filter((g) => g.gameDate === dayStr);
  };

  const formatTime = (timeStr: string | null): string => {
    if (!timeStr) return '';
    // Time comes as HH:mm:ss from backend
    const [hours, minutes] = timeStr.split(':');
    const hour = parseInt(hours, 10);
    const ampm = hour >= 12 ? 'PM' : 'AM';
    const hour12 = hour % 12 || 12;
    return `${hour12}:${minutes} ${ampm}`;
  };

  const getGameResult = (game: CalendarGame, forTeamId?: number): 'W' | 'L' | null => {
    if (game.status !== 'Final' || !forTeamId) return null;
    const isHome = game.homeTeamId === forTeamId;
    const teamScore = isHome ? game.homeScore : game.awayScore;
    const oppScore = isHome ? game.awayScore : game.homeScore;
    if (teamScore === null || oppScore === null) return null;
    return teamScore > oppScore ? 'W' : 'L';
  };

  if (loading) {
    return <div className="week-view-loading">Loading week...</div>;
  }

  if (error) {
    return <div className="week-view-error">{error}</div>;
  }

  return (
    <div className="week-view">
      <div className="week-view-grid">
        {weekDays.map((day) => {
          const dayGames = getGamesForDay(day);
          const isCurrentDay = isToday(day);
          const isSelected = isSameDay(day, currentDate);

          return (
            <div
              key={day.toISOString()}
              className={`week-day ${isCurrentDay ? 'week-day-today' : ''} ${
                isSelected ? 'week-day-selected' : ''
              }`}
              role="button"
              tabIndex={0}
              onClick={() => onDaySelect?.(day)}
              onKeyDown={(e) => {
                if (e.key === 'Enter' || e.key === ' ') {
                  e.preventDefault();
                  onDaySelect?.(day);
                }
              }}
              aria-label={`Select ${format(day, 'EEEE, MMMM d')}`}
            >
              <div className="week-day-header">
                <span className="week-day-name">{format(day, 'EEE')}</span>
                <span className={`week-day-number ${isCurrentDay ? 'today' : ''}`}>
                  {format(day, 'd')}
                </span>
              </div>

              <div className="week-day-games">
                {dayGames.length === 0 ? (
                  <div className="week-day-empty">No games</div>
                ) : (
                  dayGames.slice(0, 4).map((game) => {
                    const result = getGameResult(game, teamId);
                    return (
                      <Link
                        to={`/games/${game.id}`}
                        key={game.id}
                        className={`week-game ${result ? `week-game-${result.toLowerCase()}` : ''}`}
                        onClick={(e) => e.stopPropagation()}
                      >
                        <div className="week-game-matchup">
                          <span className="week-game-away">{game.awayTeamAbbr}</span>
                          <span className="week-game-at">@</span>
                          <span className="week-game-home">{game.homeTeamAbbr}</span>
                        </div>
                        {game.status === 'Final' ? (
                          <div className="week-game-score">
                            {game.awayScore}-{game.homeScore}
                          </div>
                        ) : game.scheduledTime ? (
                          <div className="week-game-time">
                            {formatTime(game.scheduledTime)}
                          </div>
                        ) : (
                          <div className="week-game-status">{game.status}</div>
                        )}
                      </Link>
                    );
                  })
                )}
                {dayGames.length > 4 && (
                  <div className="week-day-more">+{dayGames.length - 4} more</div>
                )}
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}

export default WeekView;
