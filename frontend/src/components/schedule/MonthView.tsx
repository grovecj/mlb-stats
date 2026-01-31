import { useState, useEffect } from 'react';
import {
  startOfMonth,
  endOfMonth,
  startOfWeek,
  endOfWeek,
  addDays,
  format,
  isSameMonth,
  isSameDay,
  isToday,
} from 'date-fns';
import { GameCount } from '../../types/game';
import { getGameCounts } from '../../services/api';
import './MonthView.css';

interface MonthViewProps {
  currentDate: Date;
  teamId?: number;
  onDaySelect?: (date: Date) => void;
}

function MonthView({ currentDate, teamId, onDaySelect }: MonthViewProps) {
  const [gameCounts, setGameCounts] = useState<GameCount[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Get month boundaries
  const monthStart = startOfMonth(currentDate);
  const monthEnd = endOfMonth(currentDate);

  // Get calendar grid boundaries (include days from prev/next month to fill weeks)
  const calendarStart = startOfWeek(monthStart, { weekStartsOn: 0 });
  const calendarEnd = endOfWeek(monthEnd, { weekStartsOn: 0 });

  // Format dates for API
  const startDateStr = format(calendarStart, 'yyyy-MM-dd');
  const endDateStr = format(calendarEnd, 'yyyy-MM-dd');

  // Generate array of all days in the calendar grid
  const calendarDays: Date[] = [];
  let day = calendarStart;
  while (day <= calendarEnd) {
    calendarDays.push(day);
    day = addDays(day, 1);
  }

  // Day of week headers
  const weekDays = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];

  useEffect(() => {
    async function fetchGameCounts() {
      setLoading(true);
      setError(null);
      try {
        const data = await getGameCounts({
          startDate: startDateStr,
          endDate: endDateStr,
          teamId,
        });
        setGameCounts(data);
      } catch (_err) {
        setError('Failed to load game counts');
        setGameCounts([]);
      } finally {
        setLoading(false);
      }
    }
    fetchGameCounts();
  }, [startDateStr, endDateStr, teamId]);

  const getCountForDay = (day: Date): GameCount | undefined => {
    const dayStr = format(day, 'yyyy-MM-dd');
    return gameCounts.find((gc) => gc.date === dayStr);
  };

  if (loading) {
    return <div className="month-view-loading">Loading month...</div>;
  }

  if (error) {
    return <div className="month-view-error">{error}</div>;
  }

  return (
    <div className="month-view">
      {/* Desktop: Calendar Grid */}
      <div className="month-view-grid">
        {/* Header row with day names */}
        <div className="month-view-header">
          {weekDays.map((dayName) => (
            <div key={dayName} className="month-header-cell">
              {dayName}
            </div>
          ))}
        </div>

        {/* Calendar days */}
        <div className="month-view-days">
          {calendarDays.map((calDay) => {
            const isCurrentMonth = isSameMonth(calDay, currentDate);
            const isCurrentDay = isToday(calDay);
            const isSelected = isSameDay(calDay, currentDate);
            const count = getCountForDay(calDay);

            return (
              <div
                key={calDay.toISOString()}
                className={`month-day ${!isCurrentMonth ? 'month-day-outside' : ''} ${
                  isCurrentDay ? 'month-day-today' : ''
                } ${isSelected ? 'month-day-selected' : ''} ${
                  count && count.totalGames > 0 ? 'month-day-has-games' : ''
                }`}
                role="button"
                tabIndex={0}
                onClick={() => onDaySelect?.(calDay)}
                onKeyDown={(e) => {
                  if (e.key === 'Enter' || e.key === ' ') {
                    e.preventDefault();
                    onDaySelect?.(calDay);
                  }
                }}
                aria-label={`Select ${format(calDay, 'EEEE, MMMM d')}${count && count.totalGames > 0 ? `, ${count.totalGames} games` : ''}`}
              >
                <span className={`month-day-number ${isCurrentDay ? 'today' : ''}`}>
                  {format(calDay, 'd')}
                </span>

                {count && count.totalGames > 0 && (
                  <div className="month-day-count">
                    {teamId ? (
                      // Show home/away breakdown when filtering by team
                      <div className="month-day-breakdown">
                        {count.homeGames > 0 && (
                          <span className="month-count-home" title="Home games">
                            H:{count.homeGames}
                          </span>
                        )}
                        {count.awayGames > 0 && (
                          <span className="month-count-away" title="Away games">
                            A:{count.awayGames}
                          </span>
                        )}
                      </div>
                    ) : (
                      // Show total count for all teams
                      <span className="month-count-total">
                        {count.totalGames} {count.totalGames === 1 ? 'game' : 'games'}
                      </span>
                    )}
                  </div>
                )}
              </div>
            );
          })}
        </div>
      </div>

      {/* Mobile: List View */}
      <div className="month-view-list">
        {calendarDays
          .filter((calDay) => {
            const count = getCountForDay(calDay);
            return isSameMonth(calDay, currentDate) && count && count.totalGames > 0;
          })
          .map((calDay) => {
            const isCurrentDay = isToday(calDay);
            const count = getCountForDay(calDay)!;

            return (
              <div
                key={calDay.toISOString()}
                className={`month-list-item ${isCurrentDay ? 'month-list-item-today' : ''}`}
                role="button"
                tabIndex={0}
                onClick={() => onDaySelect?.(calDay)}
                onKeyDown={(e) => {
                  if (e.key === 'Enter' || e.key === ' ') {
                    e.preventDefault();
                    onDaySelect?.(calDay);
                  }
                }}
                aria-label={`Select ${format(calDay, 'EEEE, MMMM d')}, ${count.totalGames} ${count.totalGames === 1 ? 'game' : 'games'}`}
              >
                <div className="month-list-date">
                  <span className="month-list-day">{format(calDay, 'EEE')}</span>
                  <span className={`month-list-number ${isCurrentDay ? 'today' : ''}`}>
                    {format(calDay, 'd')}
                  </span>
                </div>
                <div className="month-list-count">
                  {teamId ? (
                    <div className="month-list-breakdown">
                      {count.homeGames > 0 && (
                        <span className="month-count-home">
                          {count.homeGames} home
                        </span>
                      )}
                      {count.awayGames > 0 && (
                        <span className="month-count-away">
                          {count.awayGames} away
                        </span>
                      )}
                    </div>
                  ) : (
                    <span>
                      {count.totalGames} {count.totalGames === 1 ? 'game' : 'games'}
                    </span>
                  )}
                </div>
                <div className="month-list-arrow">&rarr;</div>
              </div>
            );
          })}
        {calendarDays.filter((calDay) => {
          const count = getCountForDay(calDay);
          return isSameMonth(calDay, currentDate) && count && count.totalGames > 0;
        }).length === 0 && (
          <div className="month-list-empty">No games scheduled this month</div>
        )}
      </div>
    </div>
  );
}

export default MonthView;
