import { MockMethod } from 'vite-plugin-mock'
const users = [
  { name: 'admin', password: 'Asdf_1234', token: 'admin', info: {
    name: '系统管理员'
  }},
  { name: 'editor', password: 'Asdf_1234', token: 'editor', info: {
    name: '编辑人员'
  }},
  { name: 'ccb', password: 'Asdf_1234', token: 'test', info: {
    name: '测试人员'
  }},
]
export default [
  {
    url: `/mock/user/login`,
    method: 'post',
    response: ({ body }) => {
      const user = users.find(user => {
        return body.name === user.name && body.password === user.password
      })
      if (user) {
        return {
          code: 200,
          data: {
            token: user.token,
          },
        };
      } else {
        return {
          code: 401,
          data: {},
          msg: '用户名或密码错误'
        };
      }

    }
  },
  {
    url: `/mock/user/info`,
    method: 'post',
    response: ({ body }) => {
      const { token } = body
      const info = users.find(user => {
        return user.token === token
      }).info
      if (info) {
        return {
          code: 200,
          data: {
            info: info
          },
        };
      } else {
        return {
          code: 403,
          data: {},
          msg: '无访问权限'
        };
      }

    }
  },
  {
    url: `/mock/user/out`,
    method: 'post',
    response: () => {
      return {
        code: 200,
        data: {},
        msg: 'success'
      };
    }
  },
  {
    url: `/mock/user/passwordChange`,
    method: 'post',
    response: () => {
      return {
        code: 200,
        data: {},
        msg: 'success'
      };
    }
  },
] as MockMethod[]
