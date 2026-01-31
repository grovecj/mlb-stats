import { describe, it, expect } from 'vitest'
import { screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { render } from '../../test/utils/render'
import ComparePage from '../ComparePage'

describe('ComparePage', () => {
  it('displays page title', () => {
    render(<ComparePage />)
    expect(screen.getByText('Compare Players')).toBeInTheDocument()
  })

  it('displays mode toggle buttons', () => {
    render(<ComparePage />)
    expect(screen.getByRole('button', { name: 'By Season' })).toBeInTheDocument()
    expect(screen.getByRole('button', { name: 'Career Totals' })).toBeInTheDocument()
  })

  it('displays search input when less than max players selected', () => {
    render(<ComparePage />)
    expect(screen.getByPlaceholderText(/Search for a player to add/)).toBeInTheDocument()
  })

  it('displays empty state message when no players selected', () => {
    render(<ComparePage />)
    expect(screen.getByText('Select at least 2 players to compare')).toBeInTheDocument()
  })

  it('can toggle between season and career mode', async () => {
    const user = userEvent.setup()
    render(<ComparePage />)

    const careerBtn = screen.getByRole('button', { name: 'Career Totals' })
    await user.click(careerBtn)

    expect(careerBtn).toHaveClass('active')
  })

  it('searches for players when typing in search input', async () => {
    const user = userEvent.setup()
    render(<ComparePage />)

    const searchInput = screen.getByPlaceholderText(/Search for a player to add/)
    await user.type(searchInput, 'Judge')

    // Wait for debounced search results
    await waitFor(() => {
      expect(screen.getByText('Aaron Judge')).toBeInTheDocument()
    }, { timeout: 1000 })
  })

  it('adds player when clicking search result', async () => {
    const user = userEvent.setup()
    render(<ComparePage />)

    const searchInput = screen.getByPlaceholderText(/Search for a player to add/)
    await user.type(searchInput, 'Judge')

    await waitFor(() => {
      expect(screen.getByText('Aaron Judge')).toBeInTheDocument()
    }, { timeout: 1000 })

    // Click the search result button
    const resultButton = screen.getByRole('button', { name: /Aaron Judge/ })
    await user.click(resultButton)

    // Player should now be in selected players
    await waitFor(() => {
      // The player card should now show in selected players section
      const selectedPlayerNames = screen.getAllByText('Aaron Judge')
      expect(selectedPlayerNames.length).toBeGreaterThanOrEqual(1)
    })
  })

  it('updates placeholder text after selecting first player', async () => {
    const user = userEvent.setup()
    render(<ComparePage />)

    // Add first player
    const searchInput = screen.getByPlaceholderText(/Search for a player to add/)
    await user.type(searchInput, 'Judge')

    await waitFor(() => {
      expect(screen.getByText('Aaron Judge')).toBeInTheDocument()
    }, { timeout: 1000 })

    const judgeButton = screen.getByRole('button', { name: /Aaron Judge/ })
    await user.click(judgeButton)

    // Wait for player to be added - placeholder should update
    await waitFor(() => {
      expect(screen.getByPlaceholderText(/1\/4/)).toBeInTheDocument()
    })
  })

  it('by season mode is active by default', () => {
    render(<ComparePage />)
    const seasonBtn = screen.getByRole('button', { name: 'By Season' })
    expect(seasonBtn).toHaveClass('active')
  })
})
