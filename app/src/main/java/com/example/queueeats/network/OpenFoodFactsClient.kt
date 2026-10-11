package com.example.queueeats.network

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

/**
 * Data class representing nutritional information.
 */
data class FoodProductInfo(
    val productName: String,
    val brands: String,
    val caloriesPer100g: String,
    val nutriscore: String,
    val ingredients: String
)

/**
 * Curated nutritional database for traditional Filipino canteen meals (FNRI/DOST standards),
 * combined with Open Food Facts API fallback for global products.
 */
object FilipinoFoodNutrition {
    private val nutritionMap = mapOf(
        "chicken adobo" to FoodProductInfo(
            productName = "Chicken Adobo (Adobong Manok)",
            brands = "Traditional Filipino Canteen Recipe",
            caloriesPer100g = "210 kcal / serving",
            nutriscore = "Nutri-Score: B (High Protein)",
            ingredients = "Chicken breast/thigh, soy sauce, sugarcane vinegar, garlic, bay leaves, whole black peppercorns, cooking oil."
        ),
        "sinigang na baboy" to FoodProductInfo(
            productName = "Sinigang na Baboy",
            brands = "Traditional Filipino Canteen Recipe",
            caloriesPer100g = "165 kcal / serving",
            nutriscore = "Nutri-Score: A (Rich in Vegetables & Broth)",
            ingredients = "Pork belly, tamarind soup base, tomatoes, onions, water spinach (kangkong), radish, long green chili, fish sauce."
        ),
        "lechon kawali" to FoodProductInfo(
            productName = "Lechon Kawali",
            brands = "Traditional Filipino Canteen Recipe",
            caloriesPer100g = "340 kcal / serving",
            nutriscore = "Nutri-Score: C (Energy Dense / Crispy Pork)",
            ingredients = "Pork belly slab, salt, peppercorns, garlic, bay leaves, vegetable oil for deep frying, served with liver sauce (sarsa)."
        ),
        "grilled chicken" to FoodProductInfo(
            productName = "Chicken Inasal / Grilled Chicken",
            brands = "Traditional Filipino Canteen Recipe",
            caloriesPer100g = "180 kcal / serving",
            nutriscore = "Nutri-Score: A (Lean Protein)",
            ingredients = "Chicken quarter, calamansi, lemongrass, ginger, garlic, brown sugar, vinegar, annatto oil (atsuete)."
        ),
        "mango float" to FoodProductInfo(
            productName = "Filipino Mango Float (Crema de Mangga)",
            brands = "Traditional Filipino Canteen Dessert",
            caloriesPer100g = "290 kcal / serving",
            nutriscore = "Nutri-Score: D (Sweet Dessert)",
            ingredients = "Ripe Carabao mangoes, Graham crackers, whipping cream, condensed milk."
        ),
        "house iced tea" to FoodProductInfo(
            productName = "House Blend Lemon Iced Tea",
            brands = "QueueEats Refreshments",
            caloriesPer100g = "65 kcal / glass",
            nutriscore = "Nutri-Score: C (Sweetened Beverage)",
            ingredients = "Black tea extract, cane sugar, natural lemon extract, cold water, ice."
        ),
        "sizzling pork sisig" to FoodProductInfo(
            productName = "Sizzling Pork Sisig",
            brands = "Traditional Filipino Canteen Recipe",
            caloriesPer100g = "310 kcal / serving",
            nutriscore = "Nutri-Score: C (Savory & Rich)",
            ingredients = "Pork head parts/belly, onions, calamansi juice, bird's eye chili (siling labuyo), mayonnaise, fresh egg."
        ),
        "beef pares" to FoodProductInfo(
            productName = "Beef Pares",
            brands = "Traditional Filipino Canteen Recipe",
            caloriesPer100g = "240 kcal / serving",
            nutriscore = "Nutri-Score: B (Hearty Beef Stew)",
            ingredients = "Beef brisket, star anise, ginger, garlic, onions, soy sauce, brown sugar, beef stock, toasted garlic bits, green onions."
        ),
        "special halo-halo" to FoodProductInfo(
            productName = "Special Halo-Halo",
            brands = "Traditional Filipino Canteen Dessert",
            caloriesPer100g = "320 kcal / serving",
            nutriscore = "Nutri-Score: D (Traditional Dessert)",
            ingredients = "Shaved ice, evaporated milk, sweet red beans, macapuno, saba bananas, jackfruit, leche flan, ube halaya, pinipig, ube ice cream."
        ),
        "cold buko juice" to FoodProductInfo(
            productName = "Fresh Buko Juice",
            brands = "Natural Refreshment",
            caloriesPer100g = "45 kcal / glass",
            nutriscore = "Nutri-Score: A (Natural Electrolytes)",
            ingredients = "Fresh young coconut water, tender coconut meat shreds, slight cane syrup."
        )
    )

    fun getLocalNutrition(query: String): FoodProductInfo? {
        val key = query.lowercase().trim()
        return nutritionMap[key] ?: nutritionMap.entries.find { key.contains(it.key) }?.value
    }
}

/**
 * Network client for querying Open Food Facts API with smart Filipino dish local fallback.
 */
object OpenFoodFactsClient {
    private const val TAG = "OpenFoodFactsClient"

    suspend fun searchFood(query: String): FoodProductInfo? = withContext(Dispatchers.IO) {
        // 1. Check curated Filipino canteen nutrition first (instant, reliable for local dishes)
        val localMatch = FilipinoFoodNutrition.getLocalNutrition(query)
        if (localMatch != null) {
            return@withContext localMatch
        }

        // 2. Fallback to Open Food Facts API for any other items
        try {
            val encodedQuery = URLEncoder.encode(query, "UTF-8")
            val urlString = "https://world.openfoodfacts.org/cgi/search.pl?search_terms=$encodedQuery&search_simple=1&action=process&json=1"
            val url = URL(urlString)
            val connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                setRequestProperty("User-Agent", "QueueEatsApp/1.0 (Educational Android App)")
                connectTimeout = 5000
                readTimeout = 5000
            }

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val responseJson = connection.inputStream.bufferedReader().use { it.readText() }
                val rootObj = JSONObject(responseJson)
                val products = rootObj.optJSONArray("products")

                if (products != null && products.length() > 0) {
                    val product = products.getJSONObject(0)
                    val name = product.optString("product_name", query)
                    val brand = product.optString("brands", "Local Canteen")
                    val nutriments = product.optJSONObject("nutriments")
                    val calories = nutriments?.optString("energy-kcal_100g", "N/A") ?: "N/A"
                    val nutriscore = product.optString("nutriscore_grade", "?").uppercase()
                    val ingredients = product.optString("ingredients_text_en", "Standard canteen preparation ingredients.")

                    return@withContext FoodProductInfo(
                        productName = name,
                        brands = brand,
                        caloriesPer100g = "$calories kcal / 100g",
                        nutriscore = "Nutri-Score: $nutriscore",
                        ingredients = ingredients
                    )
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching from Open Food Facts", e)
        }
        return@withContext null
    }
}
