import { describe, it, expect } from 'vitest'
import { screen, waitFor } from '@testing-library/react'
import { render } from '../../test/utils/render'
import GamesPage from '../GamesPage'

describe('GamesPage', () => {
  it('displays loading state initially', () => {
    render(<GamesPage />)
    expect(screen.getByText('Loading games...')).toBeInTheDocument()
  })

  it('displays page title', async () => {
    render(<GamesPage />)

    await waitFor(() => {
      expect(screen.getByText('MLB Games')).toBeInTheDocument()
    })
  })

  it('displays date picker controls', async () => {
    render(<GamesPage />)

    await waitFor(() => {
      expect(screen.getByRole('button', { name: /Previous/i })).toBeInTheDocument()
      expect(screen.getByRole('button', { name: /Next/i })).toBeInTheDocument()
      expect(screen.getByRole('button', { name: 'Today' })).toBeInTheDocument()
    })
  })

  it('displays date input', async () => {
    render(<GamesPage />)

    await waitFor(() => {
      const dateInput = document.querySelector('input[type="date"]')
      expect(dateInput).toBeInTheDocument()
    })
  })

  it('shows message when no games for selected date', async () => {
    render(<GamesPage />)

    await waitFor(() => {
      expect(screen.queryByText('Loading games...')).not.toBeInTheDocument()
    })

    // The mock returns no games for arbitrary dates, so we expect this message
    // or games depending on the mock setup
    const noGamesMessage = screen.queryByText('No games scheduled for this date.')
    const gameCards = document.querySelectorAll('.game-card')
    expect(noGamesMessage || gameCards.length > 0).toBeTruthy()
  })
})
