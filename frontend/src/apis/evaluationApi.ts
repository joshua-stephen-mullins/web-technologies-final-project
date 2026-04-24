import axiosClient from './axiosClient'

// Owner: Whitney (Person 3) — UC-28,29,31,33,34
export const evaluationApi = {
  getSubmitForm() {
    return axiosClient.get('/api/evaluations/submit-form')
  },
  submitBatch(data: {
    weekId: number
    evaluations: Array<{
      evaluateeId: number
      scores: Record<number, number>
      publicComments: string
      privateComments: string
    }>
  }) {
    return axiosClient.post('/api/evaluations/batch', data)
  },
  getMyReportWeeks() {
    return axiosClient.get('/api/evaluations/my-report/weeks')
  },
  getMyReport(weekId: number) {
    return axiosClient.get('/api/evaluations/my-report', { params: { weekId } })
  },
}
