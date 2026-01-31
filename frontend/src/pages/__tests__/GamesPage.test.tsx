import { describe, it, expect } from 'vitest'
import { screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { render } from '../../test/utils/render'
import GamesPage from '../GamesPage'

describe('GamesPage', () => {
  it('displays page title', async () => {
    render(<GamesPage />)

    await waitFor(() => {
      expect(screen.getByText('MLB Schedule')).toBeInTheDocument()
    })
  })

  it('displays view toggle buttons', async () => {
    render(<GamesPage />)

    await waitFor(() => {
      expect(screen.getByRole('button', { name: 'Day' })).toBeInTheDocument()
      expect(screen.getByRole('button', { name: 'Week' })).toBeInTheDocument()
      expect(screen.getByRole('button', { name: 'Month' })).toBeInTheDocument()
    })
  })

  it('displays navigation controls', async () => {
    render(<GamesPage />)

    await waitFor(() => {
      // Default view is Week, so navigation shows "Prev Week" / "Next Week"
      expect(screen.getByRole('button', { name: /Prev Week/i })).toBeInTheDocument()
      expect(screen.getByRole('button', { name: /Next Week/i })).toBeInTheDocument()
      expect(screen.getByRole('button', { name: 'Today' })).toBeInTheDocument()
    })
  })

  it('displays team filter dropdown', async () => {
    render(<GamesPage />)

    await waitFor(() => {
      expect(screen.getByRole('combobox')).toBeInTheDocument()
      expect(screen.getByText('All Teams')).toBeInTheDocument()
    })
  })

  it('week view is active by default', async () => {
    render(<GamesPage />)

    await waitFor(() => {
      const weekButton = screen.getByRole('button', { name: 'Week' })
      expect(weekButton).toHaveClass('active')
    })
  })

  it('switches to day view when Day button is clicked', async () => {
    const user = userEvent.setup()
    render(<GamesPage />)

    await waitFor(() => {
      expect(screen.getByRole('button', { name: 'Day' })).toBeInTheDocument()
    })

    await user.click(screen.getByRole('button', { name: 'Day' }))

    await waitFor(() => {
      const dayButton = screen.getByRole('button', { name: 'Day' })
      expect(dayButton).toHaveClass('active')
      // Navigation should now show "Prev" / "Next" without week label
      expect(screen.getByRole('button', { name: /^← Prev$/i })).toBeInTheDocument()
    })
  })

  it('switches to month view when Month button is clicked', async () => {
    const user = userEvent.setup()
    render(<GamesPage />)

    await waitFor(() => {
      expect(screen.getByRole('button', { name: 'Month' })).toBeInTheDocument()
    })

    await user.click(screen.getByRole('button', { name: 'Month' }))

    await waitFor(() => {
      const monthButton = screen.getByRole('button', { name: 'Month' })
      expect(monthButton).toHaveClass('active')
      // Navigation should now show "Prev Month" / "Next Month"
      expect(screen.getByRole('button', { name: /Prev Month/i })).toBeInTheDocument()
    })
  })

  it('shows games in day view after loading', async () => {
    const user = userEvent.setup()
    render(<GamesPage />)

    await waitFor(() => {
      expect(screen.getByRole('button', { name: 'Day' })).toBeInTheDocument()
    })

    await user.click(screen.getByRole('button', { name: 'Day' }))

    // Wait for games to load and verify content is shown
    await waitFor(() => {
      // Either shows games or "No games scheduled" message
      const noGamesMessage = screen.queryByText('No games scheduled for this date.')
      const gameCards = document.querySelectorAll('.game-card')
      expect(noGamesMessage !== null || gameCards.length > 0).toBeTruthy()
    })
  })
})
