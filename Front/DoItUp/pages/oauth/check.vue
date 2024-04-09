<script setup>
const route = useRoute()
const {public: config} = useRuntimeConfig();

const router = useRouter();

const headers = useRequestHeaders(['cookie'])

const {data, pending, error, refresh} = await useFetch(`${config.API_BASE_URL}/auth/user`, {
    onRequest({request, options}) {
        options.query = options.query || {}
        options.headers = options.headers || {};
        // options.headers = options.headers || {}
    },
    onRequestError({request, options, error}) {
        console.log("onRequestError")
        console.log(request)
    },
    onResponse({request, response, options}) {
        // Process the response data
        console.log("onResponse")
        console.log(response)
        console.log(response.data)
        console.log(options)
    },
    async onResponseError({request, response, options}) {
        // Handle the response errors
        console.log("onResponseError")
        console.log(response)
        console.log("response.error")
        console.log(response._data)
        console.log("response.status")
        console.log(response.status)
        if (response.status === 401) {
            if (response._data === "EXPIRED_ACCESS_TOKEN") {
                const {
                    data: refreshToken,
                    pending: tokenPending,
                    error: tokenError,
                    refresh: tokenRefresh
                } = await callApiWithAutoRefresh();
                if (tokenError.value === "success") {
                    console.log("==========refreshToken.value==========")
                    console.log(refreshToken.value)
                } else {
                    /* alert 처리 */
                }
            }
            // console.log(refreshToken.value)
        }
    },
    method: "GET",
    headers: headers,
    credentials: 'include'
})

async function callApiWithAutoRefresh() {
    return await useFetch(`${config.API_BASE_URL}/auth/reissueToken`, {
        onRequest({request, options}) {
            options.query = options.query || {}
            options.headers = options.headers || {}
            console.log("callApiWithAutoRefresh onRequest")
            console.log(request)
            // options.headers = options.headers || {}
        },
        onRequestError({request, options, error}) {
            console.log("callApiWithAutoRefresh onRequestError")
            console.log(request)
        },
        onResponse({request, response, options}) {
            // Process the response data
            console.log("callApiWithAutoRefresh onResponse")
            console.log(response)
            console.log(options)
        },
        onResponseError({request, response, options}) {
            // Handle the response errors
            console.log("callApiWithAutoRefresh onResponseError")
            console.log(response)
            console.log(response.error)

        },
        method: "POST",
        headers: headers,
        credentials: 'include'
    });

}


</script>

<template>
    <div>
        로그인 체크
    </div>
</template>
<style lang="scss" scoped>

</style>