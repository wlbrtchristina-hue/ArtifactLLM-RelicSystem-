import { createStore } from 'vuex'

export default createStore({
  state: {
    user: {
      isLoggedIn: false,
      username: '',
      role: 'guest'
    },
    navigation: {
      fromRoute: null,
      searchState: null
    }
  },
  mutations: {
    'user/login'(state, { username, role }) {
      state.user.isLoggedIn = true
      state.user.username = username
      state.user.role = role
      localStorage.setItem('isLoggedIn', 'true')
      localStorage.setItem('username', username)
      localStorage.setItem('role', role)
    },
    'user/logout'(state) {
      state.user.isLoggedIn = false
      state.user.username = ''
      state.user.role = 'guest'
      localStorage.removeItem('isLoggedIn')
      localStorage.removeItem('username')
      localStorage.removeItem('role')
    },
    'user/init'(state) {
      const isLoggedIn = localStorage.getItem('isLoggedIn') === 'true'
      const username = localStorage.getItem('username') || ''
      const role = localStorage.getItem('role') || 'guest'
      
      state.user.isLoggedIn = isLoggedIn
      state.user.username = username
      state.user.role = role
    },
    'navigation/setFromRoute'(state, route) {
      state.navigation.fromRoute = route
    },
    'navigation/setSearchState'(state, searchState) {
      state.navigation.searchState = searchState
    },
    'navigation/clearNavigation'(state) {
      state.navigation.fromRoute = null
      state.navigation.searchState = null
    }
  },
  actions: {
  },
  modules: {
  }
})