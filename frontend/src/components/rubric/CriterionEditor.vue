<template>
  <div>
    <div v-for="(criterion, index) in criteria" :key="index" class="d-flex align-center gap-2 mb-3">
      <v-text-field
        v-model="criterion.name"
        label="Criterion Name"
        density="compact"
        hide-details
        style="flex: 2"
      />
      <v-text-field
        v-model="criterion.description"
        label="Description"
        density="compact"
        hide-details
        style="flex: 3"
      />
      <v-text-field
        v-model.number="criterion.maxScore"
        label="Max Score"
        type="number"
        density="compact"
        hide-details
        style="flex: 1; min-width: 90px"
      />
      <v-btn icon="mdi-delete" variant="text" color="error" size="small" @click="remove(index)" />
    </div>
    <v-btn prepend-icon="mdi-plus" variant="tonal" size="small" @click="add">
      Add Criterion
    </v-btn>
  </div>
</template>

<script setup lang="ts">
export interface CriterionRow {
  name: string
  description: string
  maxScore: number | null
}

const criteria = defineModel<CriterionRow[]>({ default: () => [] })

function add() {
  criteria.value.push({ name: '', description: '', maxScore: null })
}

function remove(index: number) {
  criteria.value.splice(index, 1)
}
</script>
