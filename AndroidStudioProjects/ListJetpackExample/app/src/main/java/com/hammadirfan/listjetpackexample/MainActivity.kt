package com.hammadirfan.listjetpackexample

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDirection.Companion.Content
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hammadirfan.listjetpackexample.ui.theme.ListJetpackExampleTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        var ls = mutableListOf<String>()

        ls.add("World")
        ls.add("How")
        ls.add("Are")
        ls.add("You")
        super.onCreate(savedInstanceState)
        setContent {
            ListJetpackExampleTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LazyColumn {
                        items(ls.size) { index ->
                            Card(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = ls[index],
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clickable {
                                            var clickedItem = ls[index]
                                            val intent = Intent(this@MainActivity, MainActivity2::class.java)
                                            intent.putExtra("clickedText", clickedItem)
                                            startActivity(intent)
                                        }
                                )
                            }
                        }
                    }
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ListJetpackExampleTheme {
        Greeting("Android")
    }
}