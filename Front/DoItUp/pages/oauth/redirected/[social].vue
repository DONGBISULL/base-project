<script setup>
const route = useRoute()
const { public: config } = useRuntimeConfig();
console.log(config)
const { data, pending, error, refresh } = await useFetch(`${config.API_BASE_URL}/login/${route.params.social}`, {
    onRequest({ request, options }) {
        // Set the request headers
        console.log("onRequest")
        options.headers = options.headers || {}
    },
    onRequestError({ request, options, error }) {
        // Handle the request errors
        console.log("onRequestError")
        console.log(request)
    },
    onResponse({ request, response, options }) {
        // Process the response data
        console.log("onResponse")
        console.log(response)
        console.log(options)
        // localStorage.setItem('token', response._data.token)
    },
    onResponseError({ request, response, options }) {
        // Handle the response errors
        console.log("onResponseError")
        console.log(response)
    }
})
</script>

<template>
   <div>
       로그인 체크
   </div>
</template>
<style lang="scss" scoped>

</style>