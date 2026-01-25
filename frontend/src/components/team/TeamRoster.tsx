import { Link } from 'react-router-dom';
import { RosterEntry } from '../../types/team';
import DataTable from '../common/DataTable';

interface TeamRosterProps {
  roster: RosterEntry[];
}

function TeamRoster({ roster }: TeamRosterProps) {
  const columns = [
    {
      key: 'jerseyNumber',
      header: '#',
      render: (entry: RosterEntry) => entry.jerseyNumber || '-',
    },
    {
      key: 'name',
      header: 'Name',
      render: (entry: RosterEntry) => (
        <Link to={`/players/${entry.player.id}`}>{entry.player.fullName}</Link>
      ),
    },
    {
      key: 'position',
      header: 'Position',
      render: (entry: RosterEntry) => entry.position,
    },
    {
      key: 'bats',
      header: 'B/T',
      render: (entry: RosterEntry) =>
        `${entry.player.bats || '-'}/${entry.player.throwsHand || '-'}`,
    },
    {
      key: 'status',
      header: 'Status',
      render: (entry: RosterEntry) => entry.status || 'Active',
    },
  ];

  return (
    <div className="card">
      <h3 className="card-title">Roster</h3>
      {roster.length > 0 ? (
        <DataTable columns={columns} data={roster} keyExtractor={(e) => e.id} />
      ) : (
        <p>No roster data available.</p>
      )}
    </div>
  );
}

export default TeamRoster;
