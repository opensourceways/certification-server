
export default {
    namespaced: true,
    state: {
        showcookietips: false,
        cookieState: false,
        privacyContentH: 0, //隐私政策提示内容高度
        appActionbarH: 0, //底部悬浮提示框的高度
    },
    mutations: {
        setCookieState(state, param) {
            state.cookieState = param
        },
        setPrivacyContentH(state, param) {
            state.privacyContentH = param
        },
        setAppActionbarH(state, val) {
            state.appActionbarH = val
        },
        setShowcookietips(state, param) {
            state. showcookietips = param
        },
    },
    getters: {},
}