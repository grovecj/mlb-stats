# MLB Stats App - Development Guidelines

## Frontend Theme Support

This app supports both light and dark modes. When creating or modifying CSS, always use CSS variables instead of hardcoded colors to ensure proper theme support.

### Available CSS Variables

Defined in `frontend/src/index.css`:

| Variable | Light Mode | Dark Mode | Usage |
|----------|------------|-----------|-------|
| `--primary-color` | `#002d72` | `#1a4a8a` | Primary brand color, links, headings |
| `--secondary-color` | `#bf0d3e` | `#e63950` | Accents, errors, warnings |
| `--background-color` | `#f5f5f5` | `#1a1a2e` | Page background |
| `--card-background` | `#ffffff` | `#252540` | Cards, panels, modals |
| `--text-color` | `#333333` | `#e0e0e0` | Primary text |
| `--text-light` | `#666666` | `#a0a0a0` | Secondary/muted text |
| `--border-color` | `#e0e0e0` | `#3a3a5a` | Borders, dividers |
| `--success-color` | `#28a745` | `#28a745` | Success states |
| `--warning-color` | `#ffc107` | `#ffc107` | Warning states |

### Usage Examples

```css
/* DO - Use CSS variables */
.my-component {
  background: var(--card-background);
  color: var(--text-color);
  border: 1px solid var(--border-color);
}

.my-component h2 {
  color: var(--primary-color);
}

.my-component .muted {
  color: var(--text-light);
}

/* DON'T - Avoid hardcoded colors */
.my-component {
  background: #ffffff;  /* Won't adapt to dark mode */
  color: #333333;       /* Won't adapt to dark mode */
}
```

### When Hardcoded Colors Are Acceptable

- `white` or `#ffffff` for text on colored backgrounds (e.g., text on primary-colored buttons/headers)
- `rgba()` values for shadows and overlays
- Brand-specific colors that shouldn't change between themes (rare)

### Theme Toggle

The theme is toggled via a button in the header that sets `data-theme="dark"` on the document root. The CSS variables automatically update based on this attribute.
