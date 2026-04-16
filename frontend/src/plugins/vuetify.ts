import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'
import 'vuetify/styles'

export default createVuetify({
  components,
  directives,
  theme: {
    defaultTheme: 'pulse',
    themes: {
      pulse: {
        dark: true,
        colors: {
          background:       '#0C0C0E',
          surface:          '#161618',
          'surface-bright': '#212124',
          'surface-light':  '#2A2A2D',
          primary:          '#0A84FF',
          secondary:        '#5E5CE6',
          accent:           '#BF5AF2',
          error:            '#FF453A',
          warning:          '#FF9F0A',
          info:             '#64D2FF',
          success:          '#30D158',
          'on-background':  '#F5F5F7',
          'on-surface':     '#F5F5F7',
          'on-primary':     '#FFFFFF',
        },
        variables: {
          'border-color':      '#FFFFFF',
          'border-opacity':    0.08,
          'hover-opacity':     0.07,
          'activated-opacity': 0.12,
        },
      },
    },
  },
  defaults: {
    VCard:      { elevation: 0 },
    VBtn:       { elevation: 0, rounded: 'lg' },
    VTextField: { variant: 'outlined', density: 'comfortable', hideDetails: 'auto' },
    VSelect:    { variant: 'outlined', density: 'comfortable', hideDetails: 'auto' },
    VTextarea:  { variant: 'outlined', density: 'comfortable', hideDetails: 'auto' },
    VAlert:     { rounded: 'lg' },
    VChip:      { rounded: 'xl' },
    VDialog:    { maxWidth: 500 },
    VList:      { bgColor: 'transparent' },
  },
  icons: { defaultSet: 'mdi' },
})
