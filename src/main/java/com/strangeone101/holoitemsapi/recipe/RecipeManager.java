package com.strangeone101.holoitemsapi.recipe;

import com.strangeone101.holoitemsapi.item.CustomItemManager;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ShapedRecipe;

public class RecipeManager {

    public RecipeManager() {
        // Load through all recipes:
        // Probably very inefficient, so please only do this once.
        Bukkit.recipeIterator().forEachRemaining(recipe -> {
            // removing a recipe gets... difficult... without a key
            // and they should all have one anyway, so this is safe.
            NamespacedKey recipeKey;
            if(recipe instanceof Keyed keyed) {
                recipeKey = keyed.getKey();
            }
            else {
                // should be impossible
                return;
            }

            var result = recipe.getResult();
            var customItem = CustomItemManager.getCustomItem(result);
            if(customItem != null) {
                var shouldReplace = customItem.editRecipe(recipe);
                if(shouldReplace) {
                    Bukkit.removeRecipe(recipeKey);
                    Bukkit.addRecipe(recipe);
                    if(recipe instanceof ShapedRecipe shaped) {
                        System.out.println("New choicemap");
                        System.out.println(shaped.getChoiceMap());
                    }
                }
            }
        });
    }

}
