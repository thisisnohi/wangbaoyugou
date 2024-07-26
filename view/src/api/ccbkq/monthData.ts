import requestTx from '@/utils/system/request-tx'

// 门禁、移动APP考勤对比
export function getMonthDataCompare(data: object) {
  return requestTx({
    url: '/monthData/monthDataCompare',
    method: 'post',
    data
  })
}

// 月考勤数据汇总
export function monthData(data: object) {
  return requestTx({
    url: '/monthData/monthData',
    method: 'post',
    data
  })
}

// 月考勤数据明细
export function monthDataDetail(data: object) {
  return requestTx({
    url: '/monthData/monthDataDetail',
    method: 'post',
    data
  })
}



