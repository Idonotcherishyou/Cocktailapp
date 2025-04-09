package com.example.cocktailapp.data.model


import com.google.gson.annotations.SerializedName

data class Cocktail(
    @SerializedName("idDrink")
    val id: String,

    @SerializedName("strDrink")
    val name: String,

    @SerializedName("strDrinkThumb")
    val imageUrl: String,

    @SerializedName("strCategory")
    val category: String? = null,

    @SerializedName("strAlcoholic")
    val alcoholic: String? = null,

    @SerializedName("strGlass")
    val glass: String? = null,

    @SerializedName("strInstructions")
    val instructions: String? = null,

    @SerializedName("strIngredient1")
    val ingredient1: String? = null,

    @SerializedName("strIngredient2")
    val ingredient2: String? = null,

    @SerializedName("strIngredient3")
    val ingredient3: String? = null,

    @SerializedName("strIngredient4")
    val ingredient4: String? = null,

    @SerializedName("strIngredient5")
    val ingredient5: String? = null,

    @SerializedName("strMeasure1")
    val measure1: String? = null,

    @SerializedName("strMeasure2")
    val measure2: String? = null,

    @SerializedName("strMeasure3")
    val measure3: String? = null,

    @SerializedName("strMeasure4")
    val measure4: String? = null,

    @SerializedName("strMeasure5")
    val measure5: String? = null
) {
    fun getIngredientsList(): List<String> {
        return listOfNotNull(
            ingredient1, ingredient2, ingredient3,
            ingredient4, ingredient5
        ).filter { it.isNotBlank() }
    }

    fun getMeasuresList(): List<String> {
        return listOfNotNull(
            measure1, measure2, measure3,
            measure4, measure5
        ).filter { it.isNotBlank() }
    }

    fun getIngredientsWithMeasures(): List<Pair<String, String>> {
        val ingredients = getIngredientsList()
        val measures = getMeasuresList()

        return ingredients.mapIndexed { index, ingredient ->
            Pair(ingredient, measures.getOrNull(index) ?: "")
        }
    }
}