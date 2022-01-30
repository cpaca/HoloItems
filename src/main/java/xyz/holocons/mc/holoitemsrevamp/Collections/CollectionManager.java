package xyz.holocons.mc.holoitemsrevamp.Collections;


import com.strangeone101.holoitemsapi.CustomItem;
import xyz.holocons.mc.holoitemsrevamp.AbstractClass.IdolCollection;
import xyz.holocons.mc.holoitemsrevamp.Collections.EN1.EN1Collection;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CollectionManager {

    private final List<IdolCollection> idolCollections;
    private final Map<String, CustomItem> allItems;

    public CollectionManager(){
        idolCollections = List.of(
                new EN1Collection()
        );
        //Creates a map from the list of idols, key is the internal name, value is the initialized class.
        allItems = idolCollections.stream()
                .flatMap(i -> i.getAllItem().stream())
                .collect(Collectors.toMap(CustomItem::getInternalName, string -> string));
    }

    /**
     * Gets a list of all generations
     * @return a list
     */
    public List<IdolCollection> getAllGens() {
        return idolCollections;
    }

    /**
     * Gets a map that contains all custom items as its values, and their internal name as the key. Used for
     * autocompletion
     * @return All custom items that the plugin contains
     */
    public Map<String, CustomItem> getAllItems() {
        return allItems;
    }
}
