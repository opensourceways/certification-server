import Vue from 'vue'
import Vuex from 'vuex'

//引入modules
import cookieCenter from "./cookieCenter"

Vue.use(Vuex)

export default new Vuex.Store({
    state: {
        useName: {}
    },
    getters: {},
    mutations: {
        changeStatus: (state, newUse) => {
            state.userName = JSON.parse(JSON.stringify(newUse));
        }
    },
    actions: {},
    modules: {
        cookieCenter
    }
})