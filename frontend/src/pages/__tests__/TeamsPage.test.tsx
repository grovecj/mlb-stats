import { describe, it, expect } from 'vitest'
import { screen, waitFor } from '@testing-library/react'
import { render } from '../../test/utils/render'
import TeamsPage from '../TeamsPage'

describe('TeamsPage', () => {
  it('displays loading state initially', () => {
    render(<TeamsPage />)
    expect(screen.getByText('Loading teams...')).toBeInTheDocument()
  })

  it('displays teams after loading', async () => {
    render(<TeamsPage />)

    await waitFor(() => {
      expect(screen.queryByText('Loading teams...')).not.toBeInTheDocument()
    })

    expect(screen.getByText('New York Yankees')).toBeInTheDocument()
    expect(screen.getByText('Boston Red Sox')).toBeInTheDocument()
    expect(screen.getByText('Los Angeles Dodgers')).toBeInTheDocument()
  })

  it('displays page title', async () => {
    render(<TeamsPage />)

    await waitFor(() => {
      expect(screen.getByText('MLB Teams')).toBeInTheDocument()
    })
  })

  it('renders division headers for teams that exist', async () => {
    render(<TeamsPage />)

    // Wait for loading to complete
    await waitFor(() => {
      expect(screen.queryByText('Loading teams...')).not.toBeInTheDocument()
    })

    // The component should have rendered h2 elements for divisions
    const headers = document.querySelectorAll('.division-header')
    expect(headers.length).toBeGreaterThan(0)
  })
})
