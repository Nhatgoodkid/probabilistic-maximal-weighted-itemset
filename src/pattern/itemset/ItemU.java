package pattern.itemset;

public class ItemU<T>{
    // the item id
    private final T id;
    // the probability associated to that item
    private final double probability;

    /**
     * Constructor for any id type
     * @param id id of the item (any object)
     * @param probability the existential proability
     */
    public ItemU(T id, double probability) {
        this.id = id;
        this.probability = probability;
    }

    /**
     * Get the item id.
     */
    public T getId() {
        return id;
    }

    /**
     * Get the existential probability associated to this item
     * @return  the probability as a double
     */
    public double getProbability() {
        return probability;
    }

    /**
     * Get a string representation of this item.
     * @return a string
     */
    @Override
    public String toString() {
        return "" + getId() + " (" + probability + ")";
    }

    /**
     * Check if this item is equal to another.
     * @param object another item
     * @return true if equal, otherwise false.
     */
    public boolean equals(Object object) {
        ItemU<T> item = (ItemU<T>) object;
        // if the same id, then true
        if ((item.getId().equals(this.getId()))) {
            return true;
        }
        // if not the same id, then false
        return false;
    }

    /**
     * Generate an hash code for that item.
     * @return an hash code as a int.
     */
    public int hashCode() {
        String string = "" + getId();
        return string.hashCode();
    }
}
