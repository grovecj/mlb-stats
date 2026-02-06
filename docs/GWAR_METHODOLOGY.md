# gWAR (Grove WAR) Methodology

**gWAR** is our transparent, simplified Wins Above Replacement metric. Unlike fWAR (FanGraphs) and bWAR (Baseball-Reference), gWAR uses publicly documented formulas with verifiable calculations.

## Overview

gWAR measures a player's total value in wins above a replacement-level player. It combines:
- **Batting** contribution (wRAA)
- **Baserunning** contribution (wSB)
- **Fielding** contribution (OAA-based)
- **Positional** adjustment
- **Replacement** level adjustment

All components are measured in **runs above average/replacement**, then converted to wins using a runs-per-win factor (typically ~10 runs = 1 win).

---

## Position Player Formula

```
gWAR = (Batting + Baserunning + Fielding + Positional + Replacement) / Runs_Per_Win
```

### Batting (wRAA - Weighted Runs Above Average)

```
Batting = ((wOBA - lgwOBA) / wOBAScale) × PA
```

| Variable | Description | Source |
|----------|-------------|--------|
| wOBA | Player's weighted on-base average | MLB Stats API |
| lgwOBA | League average wOBA for the season | `league_constants` table |
| wOBAScale | Scaling factor (~1.15-1.18) | `league_constants` table |
| PA | Plate appearances | MLB Stats API |

**Example**: A player with .380 wOBA, 600 PA in a .310 lgwOBA environment:
```
wRAA = ((0.380 - 0.310) / 1.177) × 600 = 35.7 runs
```

### Baserunning (wSB - Weighted Stolen Bases)

```
Baserunning = (SB × 0.2) + (CS × -0.41)
```

A simplified baserunning metric using stolen bases and caught stealing.

**Example**: 30 SB, 5 CS:
```
wSB = (30 × 0.2) + (5 × -0.41) = 6.0 - 2.05 = 3.95 runs
```

### Fielding

```
Fielding = OAA × 0.9
```

| Variable | Description | Source |
|----------|-------------|--------|
| OAA | Outs Above Average | Baseball Savant |
| 0.9 | OAA to runs conversion factor | Research estimate |

OAA is considered one of the most predictive fielding metrics available. The 0.9 factor converts OAA to approximate run value.

**Example**: +10 OAA:
```
Fielding = 10 × 0.9 = 9.0 runs
```

### Positional Adjustment

```
Positional = Position_Adjustment × (Games / 162)
```

| Position | Adjustment (per 162 games) |
|----------|---------------------------|
| C | +12.5 |
| SS | +7.5 |
| 2B | +3.0 |
| CF | +2.5 |
| 3B | +2.5 |
| LF | -7.5 |
| RF | -7.5 |
| 1B | -12.5 |
| DH | -17.5 |

**Example**: Shortstop, 150 games:
```
Positional = 7.5 × (150/162) = 6.9 runs
```

### Replacement Level

```
Replacement = PA × (20.5 / 600)
```

This represents the value added above a freely available replacement-level player. The factor (20.5/600) means a full-time player (600 PA) adds 20.5 runs over replacement.

**Example**: 550 PA:
```
Replacement = 550 × (20.5/600) = 18.8 runs
```

---

## Pitcher Formula

```
gWAR = (Pitching + Replacement) / Runs_Per_Win
```

### Pitching Runs

```
Pitching = ((lgFIP - FIP) / 9) × IP
```

| Variable | Description | Source |
|----------|-------------|--------|
| FIP | Fielding Independent Pitching | MLB Stats API |
| lgFIP | League average FIP (~4.00) | Calculated from FIP constant |
| IP | Innings pitched | MLB Stats API |

**Example**: 2.80 FIP, 180 IP, 4.00 lgFIP:
```
Pitching = ((4.00 - 2.80) / 9) × 180 = 24.0 runs
```

### Pitcher Replacement Level

```
Replacement = IP × (5.5 / 200)
```

A full-time starter (200 IP) adds 5.5 runs over replacement level.

**Example**: 170 IP:
```
Replacement = 170 × (5.5/200) = 4.7 runs
```

---

## Data Sources

| Metric | Source | Update Frequency |
|--------|--------|------------------|
| WAR (official) | MLB Stats API - sabermetrics | Daily |
| wOBA, wRC+ | MLB Stats API - sabermetrics | Daily |
| FIP, xFIP | MLB Stats API - sabermetrics | Daily |
| OAA | Baseball Savant CSV | Weekly |
| xBA, xSLG, xwOBA | Baseball Savant CSV | Weekly |
| Exit velocity, barrel% | Baseball Savant CSV | Weekly |
| Sprint speed | Baseball Savant CSV | Weekly |
| League constants | FanGraphs (annual) | Pre-season |

---

## Comparison with Official WAR

| Aspect | gWAR | fWAR | bWAR |
|--------|------|------|------|
| Fielding metric | OAA | UZR | DRS |
| Baserunning | wSB (simplified) | BsR | BsR |
| Pitching basis | FIP | FIP | RA9 |
| Transparency | Fully documented | Partially documented | Complex |
| Reproducibility | 100% | ~95% | ~90% |

---

## Limitations

1. **Simplified baserunning**: gWAR only uses SB/CS, not advanced metrics like BsR or EQBRR
2. **No defensive spectrum**: Position changes within a season aren't tracked
3. **Single-position**: Uses primary position, not positional splits
4. **No league adjustments**: Doesn't account for AL/NL differences
5. **No park factors**: All runs are treated equally regardless of ballpark

---

## Example Calculations

### Example 1: Elite Position Player

Player: 2024 MVP-caliber hitter
- 636 PA, 159 games, DH
- .430 wOBA
- 50 SB, 10 CS
- +5 OAA (as RF before moving to DH)

```
Batting  = ((0.430 - 0.310) / 1.177) × 636 = 64.8 runs
Baserun  = (50 × 0.2) + (10 × -0.41) = 5.9 runs
Fielding = 5 × 0.9 = 4.5 runs
Position = -17.5 × (159/162) = -17.2 runs
Replace  = 636 × (20.5/600) = 21.7 runs

Total    = 64.8 + 5.9 + 4.5 - 17.2 + 21.7 = 79.7 runs
gWAR     = 79.7 / 10 = 8.0 WAR
```

### Example 2: Elite Starting Pitcher

Player: Cy Young candidate
- 180 IP
- 2.50 FIP

```
Pitching = ((4.00 - 2.50) / 9) × 180 = 30.0 runs
Replace  = 180 × (5.5/200) = 5.0 runs

Total    = 30.0 + 5.0 = 35.0 runs
gWAR     = 35.0 / 10 = 3.5 WAR
```

---

## API Endpoints

### Get gWAR Leaderboard

```
GET /api/players/leaders/gwar/batting?season=2024&limit=10
GET /api/players/leaders/gwar/pitching?season=2024&limit=10
```

### Get Player gWAR Breakdown

```
GET /api/players/{id}/gwar-breakdown?season=2024
```

Response:
```json
{
  "player": { "id": 123, "name": "Shohei Ohtani" },
  "season": 2024,
  "gwar": 8.0,
  "officialWar": 8.9,
  "batting": 64.8,
  "baserunning": 5.9,
  "fielding": 4.5,
  "positional": -17.2,
  "replacement": 21.7,
  "position": "DH",
  "oaa": 5,
  "methodologyUrl": "/docs/GWAR_METHODOLOGY.md"
}
```

---

## References

- [FanGraphs WAR Overview](https://library.fangraphs.com/misc/war/)
- [Baseball Savant Outs Above Average](https://baseballsavant.mlb.com/leaderboard/outs_above_average)
- [wOBA and wRAA](https://library.fangraphs.com/offense/woba/)
- [Positional Adjustments](https://library.fangraphs.com/misc/war/positional-adjustment/)
