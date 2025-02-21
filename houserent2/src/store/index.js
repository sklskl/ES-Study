import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

export default new Vuex.Store({
    state: {
        user: {
            token: null,
            role: null,
            username: ''
        }
    },
    mutations: {
        SET_USER(state, payload) {
            state.user = { ...state.user, ...payload }
        },
        CLEAR_USER(state) {
            state.user = {
                token: null,
                role: null,
                username: ''
            }
        }
    },
    actions: {
        // 登录
        login({ commit }, userInfo) {
            commit('SET_USER', userInfo)
        },
        // 退出登录
        logout({ commit }) {
            commit('CLEAR_USER')
        }
    },
    modules: {}
})
