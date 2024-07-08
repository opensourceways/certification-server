const {defineConfig} = require('@vue/cli-service')
module.exports = defineConfig({
    transpileDependencies: true,
    productionSourceMap: false,
    publicPath: './',
    outputDir: 'web/certification',
    assetsDir: 'assets',
    devServer: {
        proxy: {
            '/gw': {
                target: 'https://kw-beta.huawei.cn', //BETA
                changeOrigin: true,
            },
        },
        host: 'localhost.huawei.com',
    },
    pwa: {
        iconPaths: {
            favicon32: 'favicon.ico',
            favicon16: 'favicon.ico',
            appleTouchIcon: 'favicon.ico',
            maskIcon: 'favicon.ico',
            msTileImage: 'favicon.ico'
        }
    }
})
