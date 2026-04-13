// Shared utility for date/week formatting used across WAR and eval pages

// Format a week range for display: "02-12-2024 to 02-18-2024"
export function formatWeekRange(startDate: string, endDate: string): string {
  // TODO: Implement
  return `${startDate} to ${endDate}`
}

// Get the previous week's date range (default for WAR/eval submission)
export function getPreviousWeekRange(): { start: string; end: string } {
  // TODO: Implement
  return { start: '', end: '' }
}
