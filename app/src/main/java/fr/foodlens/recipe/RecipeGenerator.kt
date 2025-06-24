package fr.foodlens.recipe

import fr.foodlens.openfoodfacts.Product

class RecipeGenerator(

) {
    fun generateStringForPrompt(products:List<Product>, allergens: List<Allergens>):String{
        return "Generate a simple recipe, giving the few steps and used products, using : "+productsToUse(products)+
                "You need to avoid the following allergens: "+allergenToAvoid(allergens)
    }

    private fun productsToUse(products: List<Product>):String{
        val names = products.map{"- "+it.brands + "|" +it.name+ "|"+it.quantity+" available"}
        val results = names.joinToString { " " }
        return results
    }

    private fun allergenToAvoid(allergens: List<Allergens>):String{
        val names = allergens.map{"-"+it.name}
        val results = names.joinToString { " " }
        return results
    }

    //Implémenter modèles et appels à Ollama
}