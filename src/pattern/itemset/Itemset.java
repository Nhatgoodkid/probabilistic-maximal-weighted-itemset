package pattern.itemset;

import pattern.AbstractOrderedItemset;

import java.util.List;

public class Itemset extends AbstractOrderedItemset {
    /** the array of items **/
    public int[] itemset;

    /**  the support of this itemset */
    public int support = 0;

    /**
     * Get the items as array
     * @return the items
     */
    public int[] getItems() {
        return itemset;
    }

    /**
     * Constructor
     */
    public Itemset(){
        itemset = new int[]{};
    }

    /**
     * Constructor
     * @param item an item that should be added to the new itemset
     */
    public Itemset(int item){
        itemset = new int[]{item};
    }

    /**
     * Constructor
     * @param items an array of items that should be added to the new itemset
     */
    public Itemset(int [] items){
        this.itemset = items;
    }

    /**
     * Constructor
     * @param support the support of the itemset
     */
    public Itemset(List<Integer> itemset, int support){
        this.itemset = new int[itemset.size()];
        int i = 0;
        for (Integer item : itemset) {
            this.itemset[i++] = item.intValue();
        }
        this.support = support;
    }

    /**
     * Get the support of this itemset
     */
    public int getAbsoluteSupport(){
        return support;
    }

    /**
     * Get the size of this itemset
     */
    public int size() {
        return itemset.length;
    }

    /**
     * Get the item at a given position in this itemset
     */
    public Integer get(int position) {
        return itemset[position];
    }

    /**
     * Set the support of this itemset
     * @param support the support
     */
    public void setAbsoluteSupport(Integer support) {
        this.support = support;
    }

}
