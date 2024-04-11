// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
    devtools: {
      enabled: true,

      timeline: {
        enabled: true,
      },
    },
    runtimeConfig: {
        // The private keys which are only available within server-side
        // Keys within public, will be also exposed to the client-side
        public: {
            API_BASE_URL: process.env.API_BASE_URL || 'http://localhost:8080',
            KAKAO_LOGIN_PREFIX: process.env.KAKAO_LOGIN_PREFIX || '/oauth2/authorization/kakao',
            GOOGLE_LOGIN_PREFIX: process.env.GOOGLE_LOGIN_PREFIX || '/oauth2/authorization/google',
        }
    },
    modules: [
        '@nuxt/devtools',
    ],
    // vite: {
    // server: {
    //   proxy: {
    //     "/api": {
    //       target: "http://localhost:8080",
    //       changeOrigin: true,
    //     },
    //   },
    // },
    // },
})