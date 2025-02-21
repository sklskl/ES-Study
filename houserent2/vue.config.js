module.exports = {
  publicPath: '/',
  outputDir: 'dist',
  assetsDir: 'static',
  lintOnSave: true,
  runtimeCompiler: false,
  productionSourceMap: false,
  devServer: {
    open: true,
    host: '0.0.0.0',
    port: 8080,
    https: false,
    proxy: {
      '/api': {
        target: 'http://10.1.16.152:8081/springboot503nz/',
        changeOrigin: true,
        pathRewrite: {
          '^/api': '',
        },
      },
    },
  },
  configureWebpack: {
    resolve: {
      alias: {
        '@': require('path').resolve(__dirname, 'src'),
      },
    },
  },
  css: {
    loaderOptions: {
      sass: {
        additionalData: `@import "@/styles/global.scss";`,
      },
    },
  },
}
