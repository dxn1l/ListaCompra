package com.example.listacompra.Screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.listacompra.DataBase.Product
import com.example.listacompra.DataBase.ShopDatabaseHelper2

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListScreen() {
    val context = LocalContext.current
    val dbHelper = remember { ShopDatabaseHelper2(context) }
    var products by remember { mutableStateOf(dbHelper.getAllProducts()) }
    var showDialog by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    MaterialTheme(colorScheme = darkColorScheme()) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Lista de la compra") }
                )
            }
        ) { padding ->
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
            ) {
                Button(
                    onClick = { showDialog = true },
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                ) {
                    Text("Añadir producto")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Box(modifier = Modifier.weight(1f)) {
                    LazyColumn {
                        items(products) { product ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "${product.name} - ${product.quantity} x ${product.price}")
                                    IconButton(onClick = {
                                        dbHelper.deleteProduct(product.id)
                                        products = dbHelper.getAllProducts()
                                    }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                                    }
                                }
                            }
                        }
                    }
                }

                val validProducts = products.filter { it.quantity > 0 && it.price > 0.0 }
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Total Productos: ${validProducts.size}",
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = "Precio total: ${validProducts.sumOf { it.price * it.quantity }}",
                        )
                    }
                }
            }

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("Añadir Producto") },
                    text = {
                        Column {
                            TextField(
                                value = name,
                                onValueChange = { name = it },
                                label = { Text("Nombre") }
                            )
                            TextField(
                                value = quantity,
                                onValueChange = { quantity = it },
                                label = { Text("Cantidad") }
                            )
                            TextField(
                                value = price,
                                onValueChange = { price = it },
                                label = { Text("Precio") }
                            )
                            if (isError) {
                                Text(
                                    text = "Por favor, introduce un nombre válido",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    },
                    confirmButton = {
                        Button(onClick = {
                            if (name.isNotBlank()) {
                                val quantityInt = quantity.toIntOrNull() ?: 0
                                val priceDouble = price.toDoubleOrNull() ?: 0.0
                                dbHelper.addProduct(Product(0, name, quantityInt, priceDouble))
                                products = dbHelper.getAllProducts()
                                showDialog = false
                                isError = false
                            } else {
                                isError = true
                            }
                        }) {
                            Text("Añadir")
                        }
                    },
                    dismissButton = {
                        Button(onClick = { showDialog = false }) {
                            Text("Cancelar")
                        }
                    }
                )
            }
        }
    }
}