package com.example.myapplication

import android.R.attr.text
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.draw.alpha

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ServicesApp()
        }
    }
}

@Composable
fun ServicesApp() {
    val navController = rememberNavController()
    val cartItems = remember { mutableStateListOf<CartItem>() }

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "catalog",
            modifier = Modifier.padding(padding)
        ) {
            composable("catalog") { CatalogScreen(navController, cartItems) }
            composable("cart") { CartScreen(navController, cartItems) }
            composable("profile") { ProfileScreen(navController) }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Catalog") },
            label = { Text("Каталог") },
            selected = navController.currentBackStackEntry?.destination?.route == "catalog",
            onClick = { navController.navigate("catalog") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Cart") },
            label = { Text("Корзина") },
            selected = navController.currentBackStackEntry?.destination?.route == "cart",
            onClick = { navController.navigate("cart") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("Профиль") },
            selected = navController.currentBackStackEntry?.destination?.route == "profile",
            onClick = { navController.navigate("profile") }
        )
    }
}

@Composable
fun CatalogScreen(navController: NavController, cartItems: SnapshotStateList<CartItem>) {
    val services = listOf(
        Service(1, "Решить вопрос", 25.0, "30 мин"),
        Service(2, "Решить 2 вопроса по цене 1", 20.0, "45 min"),
        Service(3, "Сопровождение", 50.0, "60 min"),
        Service(4, "эээ не придумал", 25.0, "60 min")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Каталог",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(services) { service ->
                ServiceCard(service, cartItems)
            }
        }
    }
}

@Composable
fun ServiceCard(service: Service, cartItems: SnapshotStateList<CartItem>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(text = service.name, fontSize = 18.sp, fontWeight = FontWeight.Medium)
            Text(text = "$${service.price}", fontSize = 16.sp, color = Color.Gray)
            Text(text = service.duration, fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    val existingItem = cartItems.find { it.service.id == service.id }
                    if (existingItem != null) {
                        existingItem.quantity++
                    } else {
                        cartItems.add(CartItem(service, 1))
                    }
                },
                modifier = Modifier.align(Alignment.End),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.DarkGray,
                    contentColor = Color.White
                )
            ) {
                Text("Добавить")
            }
        }
    }
}

@Composable
fun CartScreen(navController: NavController, cartItems: SnapshotStateList<CartItem>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Корзина",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        if (cartItems.isEmpty()) {
            Text(
                text = "Тут пусто :)",
                modifier = Modifier.fillMaxSize(),
                textAlign = TextAlign.Center
            )
        } else {
            Column {
                cartItems.forEach { item ->
                    CartItemRow(item, cartItems)
                }
                Spacer(modifier = Modifier.height(16.dp))
                val total = cartItems.sumOf { it.service.price * it.quantity }
                Text(
                    text = "Итого: $${String.format("%.2f", total)}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { /* TODO: Оформление заказа */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.DarkGray,
                        contentColor = Color.White
                    )
                ) {
                    Text("Оплатить")
                }
            }
        }
    }
}

@Composable
fun CartItemRow(item: CartItem, cartItems: SnapshotStateList<CartItem>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = item.service.name, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Text(text = "$${item.service.price}", fontSize = 14.sp, color = Color.Gray)
            Text(text = item.service.duration, fontSize = 14.sp, color = Color.Gray)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = "Decrease",
                modifier = Modifier
                    .clickable {
                        if (item.quantity > 1) item.quantity-- else cartItems.remove(item)
                    }
                    .padding(8.dp)
            )
            Text(text = "${item.quantity}", fontSize = 16.sp, modifier = Modifier.padding(horizontal = 8.dp))
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = "Increase",
                modifier = Modifier
                    .clickable { item.quantity++ }
                    .padding(8.dp)
            )
        }
    }
}
@Composable
fun ProfileScreen(navController: NavController) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Профиль",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Text(text = "Владимир Путин", fontSize = 18.sp, modifier = Modifier.padding(bottom = 8.dp).alpha(0.6f))
            Text(text = "+7 777 777 77 77", fontSize = 18.sp, modifier = Modifier.padding(bottom = 8.dp).alpha(0.6f))
            Text(text = "kreml@vpered.ru", fontSize = 18.sp, modifier = Modifier.alpha(0.6f))
        }

        Column(
            modifier = Modifier
                .align(BottomCenter)
                .fillMaxWidth()
                .padding(16.dp)
                .alpha(0.6f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Ответы на вопросы", fontSize = 18.sp,)
            Text(text = "Политика конфиденциальности", fontSize = 18.sp)
            Text(text = "Пользовательское соглашение", fontSize = 18.sp)
            Text(text = "Выйти", fontSize = 18.sp, color = Color.Red)
        }
    }
}