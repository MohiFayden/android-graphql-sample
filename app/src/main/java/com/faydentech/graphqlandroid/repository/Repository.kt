package com.faydentech.graphqlandroid.repository

import android.util.Log
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Operation
import com.apollographql.apollo3.api.Optional
import com.faydentech.graphqlandroid.AddBookMutation
import com.faydentech.graphqlandroid.DeleteBookMutation
import com.faydentech.graphqlandroid.GetBooksQuery
import com.faydentech.graphqlandroid.GetBooksQuery.Book
import com.faydentech.graphqlandroid.GetFilteredBooksQuery
import com.faydentech.graphqlandroid.UpdateBookMutation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object Repository {

    private suspend fun <T : Operation.Data> makeApolloCall(
        call: suspend () -> ApolloResponse<T>
    ): T? {
        return withContext(Dispatchers.IO) {
            try {
                val response = call()
                if (response.hasErrors()) {
                    Log.e("Repository", "Apollo Error: ${response.errors}")
                    null
                } else {
                    response.data
                }
            } catch (e: Exception) {
                Log.e("Repository", "Apollo Exception: ${e.message}", e)
                null
            }
        }
    }

    /**
     * Fetch list of all books
     */
    suspend fun fetchBooks(): List<Book?> {
        return makeApolloCall { ApolloClientProvider.client.query(GetBooksQuery()).execute() }
            ?.books ?: emptyList()
    }

    /**
     * Fetch list of books based on the filters
     */
    suspend fun fetchFilteredBooks(
        genre: String?,
        minPrice: Double?,
        maxPrice: Double?
    ): List<GetFilteredBooksQuery.Book?> {
        return makeApolloCall {
            ApolloClientProvider.client.query(
                GetFilteredBooksQuery(
                    genre = genre?.let { Optional.Present(it) } ?: Optional.Absent,
                    minPrice = minPrice?.let { Optional.Present(it) } ?: Optional.Absent,
                    maxPrice = maxPrice?.let { Optional.Present(it) } ?: Optional.Absent,
                )
            ).execute()
        }?.books ?: emptyList()
    }

    /**
     * Add a book
     */
    suspend fun addBook(
        title: String,
        authorId: String,
        genre: String,
        price: Double
    ): AddBookMutation.AddBook? {
        return makeApolloCall {
            ApolloClientProvider.client.mutation(
                AddBookMutation(
                    title = title,
                    authorId = authorId,
                    genre = genre,
                    price = price
                )
            ).execute()
        }?.addBook
    }

    /**
     * Update book based on it's ID
     */
    suspend fun updateBook(
        id: String,
        title: String?,
        genre: String?,
        price: Double?
    ): UpdateBookMutation.UpdateBook? {
        return makeApolloCall {
            ApolloClientProvider.client.mutation(
                UpdateBookMutation(
                    id = id,
                    title = title?.let { Optional.Present(it) } ?: Optional.Absent,
                    genre = genre?.let { Optional.Present(it) } ?: Optional.Absent,
                    price = price?.let { Optional.Present(it) } ?: Optional.Absent,
                )
            ).execute()
        }?.updateBook
    }

    /*
     * Delete book based on it's ID
     */
    suspend fun deleteBook(id: String): String {
        return makeApolloCall {
            ApolloClientProvider.client.mutation(
                DeleteBookMutation(id = id)
            ).execute()
        }?.deleteBook ?: ""
    }
}