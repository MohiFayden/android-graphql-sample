package com.faydentech.graphqlandroid.repository

import com.apollographql.apollo3.ApolloClient

object ApolloClientProvider {

    val client: ApolloClient = ApolloClient.Builder()
        .serverUrl("http://10.0.2.2:4000")
        .build()
}