package com.faydentech.graphqlandroid.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.faydentech.graphqlandroid.GetBooksQuery.*
import com.faydentech.graphqlandroid.repository.Repository
import com.faydentech.graphqlandroid.ui.theme.GraphQLAndroidTheme
import kotlinx.coroutines.launch

@Composable
fun BooksListScreen() {
    var books by remember { mutableStateOf<List<Book?>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            books = Repository.fetchBooks()
        }
    }

    ContentList(books.filterNotNull())
}

@Composable
private fun ContentList(books: List<Book>) {
    if (books.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(books) {
                CountryItem(it)
            }
        }
    }
}

@Composable
private fun CountryItem(book: Book) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "${book.title} by ${book.author?.name}",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "Price: ${book.price}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

//----------------Preview----------------
@Preview(showBackground = true)
@Composable
private fun PreviewCountryItem() {
    val countries = (1..5).map {
        Book(
            id = "100",
            title = "Amazing Book",
            genre = "Some Genre",
            price = 23.0,
            author = Author(name = "Mohi Fayden")
        )
    }

    GraphQLAndroidTheme {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(countries) {
                CountryItem(it)
            }
        }
    }
}