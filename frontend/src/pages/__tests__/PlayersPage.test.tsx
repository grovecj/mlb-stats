import { describe, it, expect } from 'vitest'
import { screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { render } from '../../test/utils/render'
import PlayersPage from '../PlayersPage'

describe('PlayersPage', () => {
  it('displays loading state initially', () => {
    render(<PlayersPage />)
    expect(screen.getByText('Loading players...')).toBeInTheDocument()
  })

  it('displays players after loading', async () => {
    render(<PlayersPage />)

    await waitFor(() => {
      expect(screen.queryByText('Loading players...')).not.toBeInTheDocument()
    })

    expect(screen.getByText('Aaron Judge')).toBeInTheDocument()
    expect(screen.getByText('Gerrit Cole')).toBeInTheDocument()
    expect(screen.getByText('Juan Soto')).toBeInTheDocument()
  })

  it('displays page title', async () => {
    render(<PlayersPage />)

    await waitFor(() => {
      expect(screen.getByText('MLB Players')).toBeInTheDocument()
    })
  })

  it('displays search form', async () => {
    render(<PlayersPage />)

    await waitFor(() => {
      expect(screen.getByPlaceholderText('Search players by name...')).toBeInTheDocument()
      expect(screen.getByRole('button', { name: 'Search' })).toBeInTheDocument()
    })
  })

  it('shows pagination controls', async () => {
    render(<PlayersPage />)

    await waitFor(() => {
      expect(screen.getByText(/Page 1 of/)).toBeInTheDocument()
      expect(screen.getByRole('button', { name: 'Previous' })).toBeInTheDocument()
      expect(screen.getByRole('button', { name: 'Next' })).toBeInTheDocument()
    })
  })

  it('disables Previous button on first page', async () => {
    render(<PlayersPage />)

    await waitFor(() => {
      const prevButton = screen.getByRole('button', { name: 'Previous' })
      expect(prevButton).toBeDisabled()
    })
  })

  it('allows searching for players', async () => {
    const user = userEvent.setup()
    render(<PlayersPage />)

    await waitFor(() => {
      expect(screen.queryByText('Loading players...')).not.toBeInTheDocument()
    })

    const searchInput = screen.getByPlaceholderText('Search players by name...')
    const searchButton = screen.getByRole('button', { name: 'Search' })

    await user.type(searchInput, 'Judge')
    await user.click(searchButton)

    await waitFor(() => {
      expect(screen.getByText('Aaron Judge')).toBeInTheDocument()
    })
  })
})
