import requestTx from '@/utils/system/request-tx'

// 门禁、移动APP考勤对比
export function getMonthDataCompare(data: object) {
  return requestTx({
    url: '/monthData/monthDataCompare',
    method: 'post',
    data
  })
}

// 导出门禁、移动考勤对比数据
export function exportMonthDataCompareApi(data: object) {
  return requestTx({
    url: '/monthData/exportMonthDataCompare',
    method: 'post',
    data,
    responseType: 'blob'
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
// 导出月考勤数据汇总
export function exportMonthDataApi(data: object) {
  return requestTx({
    url: '/monthData/exportMonthData',
    method: 'post',
    data,
    responseType: 'blob'
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

// 导出月考勤数据明细
export function exportMonthDataDetailApi(data: object) {
  return requestTx({
    url: '/monthData/exportMonthDataDetail',
    method: 'post',
    data,
    responseType: 'blob'
  })
}

// 结算考勤数据汇总
export function jsMonthData(data: object) {
  return requestTx({
    url: '/quarterJs/monthData',
    method: 'post',
    data
  })
}

// 导出结算考勤数据汇总
export function exportJsMonthDataApi(data: object) {
  return requestTx({
    url: '/quarterJs/exportMonthData',
    method: 'post',
    data,
    responseType: 'blob'
  })
}

// 导出项目结算月考勤数据汇总
export function exportProjectJsMonthDataApi(data: object) {
  return requestTx({
    url: '/quarterJs/exportProjectMonthData',
    method: 'post',
    data,
    responseType: 'blob'
  })
}

// 结算考勤数据明细
export function jsMonthDataDetail(data: object) {
  return requestTx({
    url: '/quarterJs/monthDataDetail',
    method: 'post',
    data
  })
}

// 导出结算考勤数据汇总
export function exportJsMonthDataDetailApi(data: object) {
  return requestTx({
    url: '/quarterJs/exportMonthDetail',
    method: 'post',
    data,
    responseType: 'blob'
  })
}

// 导出结算考勤数据汇总
export function exportMonthDetailByProjectApi(data: object) {
  return requestTx({
    url: '/quarterJs/exportMonthDetailByProject',
    method: 'post',
    data,
    responseType: 'blob'
  })
}



// 判断人、工作日是否重复
export function userWorkRepeat(data: object) {
  return requestTx({
    url: '/quarterJs/userWorkRepeat',
    method: 'post',
    data
  })
}

// 结算考勤有，月份考勤无
export function jsWithOutApp(data: object) {
  return requestTx({
    url: '/quarterJs/jsWithOutApp',
    method: 'post',
    data
  })
}

// 月份考勤有，结算考勤无
export function appWithOutJs(data: object) {
  return requestTx({
    url: '/quarterJs/appWithOutJs',
    method: 'post',
    data
  })
}

// app与结算考勤不一致
export function daysDiff(data: object) {
  return requestTx({
    url: '/quarterJs/daysDiff',
    method: 'post',
    data
  })
}

