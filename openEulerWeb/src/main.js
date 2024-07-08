import Vue from 'vue';
import App from './App.vue';
import router from './router';
import store from './store';
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import '@/assets/css/common.less';
import axios from "axios";

Vue.prototype.axios = axios;
axios.defaults.baseURL = '/qw/oem/com.huawei.cabg.oem:OpenEuler/certification';

axios.interceptors.request.use(async (config) => {
    //为请求头添加Authorization字段为服务端返回的token
    config.headers.Authorization = localStorage.getItem('user')

    function getCookie(name) {
        const pattern = new RegExp(name + '=([^;]*)');
        const matches = document.cookie.match(pattern);
        if (matches) {
            return matches[1];
        }
        return null;
    }

    config.headers['x-xsrf-token'] = getCookie('XSRF-TOKEN')

    //return config是固定用法 必须有返回值
    return config;
});

axios.interceptors.response.use(
    (res) => {
        if (
            res.data.code === 500 &&
            res.data.message === "You haven't signed the privacy protocol yet" &&
            res.config.url === '/user/getUserInfo'
        ) {
            ElementUI.Message.error(
                '您没有签署隐私政策协议，请先签署隐私政策协议后重试！'
            );
            setTimeout(() => {
                router.push({
                    path: '/priacyPolicy',
                    query: {
                        toSign: 'true'
                    },
                });
            }, 2000);
        }
        return res;
    },
    function (error) {
        if (401 === error.response.status) {
            window.location = '';
        } else {
            return Promise.reject(error);
        }
    }
)

axios.defaults.headers.common['X-Requested-with'] = 'XMLHttpRequest';
axios.defaults.xsrfCookieName = 'MyCustomXsrfCookieName';
axios.defaults.xsrfHeaderName = 'MyCustomXsrfHeaderName';

router.afterEach(() => {
    window.scrollTo(0, 0);
});

Vue.use(ElementUI);

Vue.config.productionTip = false;

new Vue({
    router,
    store,
    render: (h) => h(App),
}).$mount('#app');
