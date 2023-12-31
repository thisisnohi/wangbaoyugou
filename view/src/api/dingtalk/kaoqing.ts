import requestTx from '@/utils/system/request-tx'

export function dingTalkPageList(page: object, data: object) {
  return requestTx({
    url: '/dtKq/dingTalkPageList',
    method: 'post',
    data: {
      page,
      data
    }
  })
}

// 同步审批实例
export function syncProcInfos(data: object) {
  return requestTx({
    url: '/dtAprove/syncProcInfos',
    method: 'post',
    data: {
      data
    }
  })
}

// 同步考勤数据
export function synUserKq(data: object) {
  return requestTx({
    url: '/dtKq/synUserKq',
    method: 'post',
    data: {
      data
    }
  })
}

// 刷新部门
export function syncDept(searchParam: object) {
  return requestTx({
    url: '/dtDetp/refresh',
    method: 'post',
    data: searchParam
  })
}

// 刷新考勤组
export function syncKqGroup(searchParam: object) {
  return requestTx({
    url: '/dtGroup/refresh',
    method: 'post',
    data: searchParam
  })
}

// 刷新考勤组
export function syncUser(searchParam: object) {
  return requestTx({
    url: '/dtUser/refresh',
    method: 'post',
    data: searchParam
  })
}

export function empWorkSheetDetail(data: object) {
  return requestTx({
    url: '/dtKq/empWorkSheetDetail',
    method: 'post',
    data: {
      data
    }
  })
}

export function exportSheet(searchParam: object) {
  return requestTx({
    url: '/dtKq/exportSheet',
    method: 'post',
    data: {
      data: searchParam
    },
    responseType: 'blob'
  })
}

export function empWorkSheet(searchParam: object) {
  return requestTx({
    url: '/dtKq/empWorkSheet',
    method: 'post',
    data: searchParam
  })

}
export function userTokenQuery(page: object, searchParam: object) {
  return requestTx({
    url: '/teamKaoQin/userInfo/query',
    method: 'post',
    data: {
      page,
      searchParam
    }
  })

}
export function userInfoSave(data: object) {
  return requestTx({
    url: '/teamKaoQin/userInfo/save',
    method: 'post',
    data: {
      data
    }
  })

}