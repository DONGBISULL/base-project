export function useCustomFetch(url, opts) {
    const accessToken = useCookie('AccessToken')
    const refreshToken = useCookie('RefreshToken')
    const options = {
        retryStatusCodes: [401],
        retry: 1,
        onRequest({options}) {
            if (accessToken && accessToken.value) {
                options.headers = {
                    ...options.headers,
                    Authorization: `Bearer ${accessToken.value}`
                }
            }
        },
        async onResponseError({request, response, options}) {
            // Handle the response errors
            console.log("onResponseError")
            console.log(response)
            console.log("response.error")
            console.log(response.error)
            if (response.status === 401) {
                // 새로운 액세스 토큰을 사용하여 요청을 재시도
                const response = await $fetch(`${API_BASE_URL}/auth/reissueToken`, {
                    method: 'GET',
                    credentials: 'include',
                }).then(
                    (response) => {
                        console.log(response)
                        accessToken.value = response.accessToken;
                        options.headers['Authorization'] = `Bearer ${response.accessToken}`;
                        return response.accessToken;
                    }
                ).catch((error) => {
                    console.log(error, 'ErrorRefreshToken')
                    return error
                })
            }
        },
        ...opts
    }
    const headers = useRequestHeaders(['cookie'])

    if (headers && headers.cookie) {
        options.headers = {
            ...options.headers,
            Cookie: headers.cookie,
        };
    } else {
        options.headers = {
            ...options.headers,
        };
    }

    return useFetch(url, {
        ...options,
    })
}
