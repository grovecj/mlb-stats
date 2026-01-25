export interface Player {
  id: number;
  mlbId: number;
  fullName: string;
  firstName: string;
  lastName: string;
  jerseyNumber: string;
  position: string;
  positionType: string;
  bats: string;
  throwsHand: string;
  birthDate: string;
  height: string;
  weight: number;
  mlbDebutDate: string;
  active: boolean;
  headshotUrl: string | null;
}
