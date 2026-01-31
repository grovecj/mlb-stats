/**
 * MLB season utilities
 */

/**
 * The earliest year with reliable MLB stats data in our system.
 * Used for season selectors and historical data ranges.
 */
export const EARLIEST_MLB_SEASON = 2015;

/**
 * Get the default season to display based on current date.
 * Returns previous year if we're in Jan-Mar (before season typically starts),
 * otherwise returns current year.
 */
export function getDefaultSeason(): number {
  const now = new Date();
  const month = now.getMonth();
  return month < 3 ? now.getFullYear() - 1 : now.getFullYear();
}

/**
 * Generate an array of season years from current season down to earliest.
 */
export function getSeasonOptions(currentSeason?: number): number[] {
  const endSeason = currentSeason ?? getDefaultSeason();
  const seasons: number[] = [];
  for (let year = endSeason; year >= EARLIEST_MLB_SEASON; year--) {
    seasons.push(year);
  }
  return seasons;
}
