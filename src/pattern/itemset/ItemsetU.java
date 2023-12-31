package pattern.itemset;

import java.text.DecimalFormat;
import java.util.*;

public class ItemsetU<T extends Comparable<T>>{
    // an itemset is an ordered list of items
    private List<ItemU<T>> items = new ArrayList<>();
    // the expected support
    private double expectedsupport = 0;
    /**
     * Default constructor
     */
    public ItemsetU(){

    }

    /**
     * Get the expected support of this itemset.
     * @return expected support value.
     */
    public double getExpectedSupport() {
        return expectedsupport;
    }

    /**
     * Get the expected support as a five decimals string
     * @return a string
     */
    public String getSupportAsString() {
        DecimalFormat format = new DecimalFormat();
        format.setMinimumFractionDigits(0);
        format.setMaximumFractionDigits(5);
        return format.format(expectedsupport);
    }

    /**
     * Increase the expected support of this itemset by a given amount.
     * @param supp the amount of support.
     */
    public void increaseSupportBy(double supp) {
        expectedsupport += supp;
    }


    /**
     * Add an item to that itemset
     * @param value the item to be added
     */
    public void addItem(ItemU<T> value){
        items.add(value);

    }

    /**
     * Get items from that itemset.
     * @return a list of integers (items).
     */
    public List<ItemU<T>> getItems(){
        return items;
    }

    /**
     * Get the item at at a given position in that itemset
     * @param index the position
     * @return the item (Integer)
     */
    public ItemU<T> get(int index){
        return items.get(index);
    }

    /**
     * print this itemset to System.out.
     */
    public void print(){
        System.out.print(toString());
    }

    /**
     * Print the items in this itemset to System.out.
     */
    public void printWithoutSupport(){
        StringBuilder r = new StringBuilder ();
        for(ItemU<T> attribute : items){
            r.append(attribute.getId());
            r.append(' ');
        }
        System.out.print(r);
    }

    /**
     * Get a string representation of the items in this itemset.
     */
    public String toString(){
        StringBuilder r = new StringBuilder ();
        for(ItemU<T> attribute : items){
            r.append(attribute.toString());
            r.append(' ');
        }
        return r.toString();
    }

    /**
     * Check if this itemset contains a given item.
     * @param item the item
     * @return true, if yes, otherwise false.
     */
    public boolean contains(ItemU<T> item) {
        return items.contains(item);
    }

    /**
     * Checks if this itemset is lexically smaller than a given itemset of the same size.
     * @param itemset2 a given itemset
     * @return true, if yes, otherwise, false
     */
    boolean isLexicallySmallerthan(ItemsetU<T> itemset2){
        // for each item in this itemset
        for(int i=0; i< items.size(); i++){
            // if it is larger than the item at the same position in itemset2
            // return false
            if(items.get(i).getId().compareTo(itemset2.items.get(i).getId()) > 0 ){
                return false;
            }
            // if it is smaller than the item at the same position in itemset2
            // return true
            else if(items.get(i).getId().compareTo(itemset2.items.get(i).getId())  < 0){
                return true;
            }
        }
        // otherwise return true
        return true;
    }

    /**
     * Check if this itemset is equal to another one.
     * @param itemset2 the other itemset
     * @return true if yes, otherwise false
     */
    public boolean isEqualTo(ItemsetU<T> itemset2){
        // if not the same size, they can't be equal!
        if(items.size() != itemset2.items.size()){
            return false;
        }
        // for each item
        for(ItemU<T> val : items){
            // check if it is contained in the other itemset
            // if not they are not equal.
            if(!itemset2.contains(val)){
                return false;
            }
        }
        // they are equal, then return true
        return true;
    }

    /**
     * Set the expected support to a given value.
     * @param expectedsupport the value
     */
    void setExpectedSupport(double expectedsupport) {
        this.expectedsupport = expectedsupport;
    }

    /**
     * Make a copy of an itemset but exclude a given item
     * @param itemToExclude the item
     * @return the copy
     */
    public ItemsetU<T> cloneItemSetMinusOneItem(ItemU<T> itemToExclude){
        // create a new itemset
        ItemsetU<T> itemset = new ItemsetU<T>();
        // for each item
        for(ItemU<T> item : items){
            // if it is not the one to be excluded, then add it
            if(!item.equals(itemToExclude)){
                itemset.addItem(item);
            }
        }
        // return the itemset
        return itemset;
    }

    /**
     * Get the number of items in this itemset
     * @return the item count (int)
     */
    public int size(){
        return items.size();
    }

    /**
     * check if the item from this itemset are all the same as those of itemset2 
     * except the last item 
     * and that itemset2 is lexically smaller than this itemset. If all these conditions are satisfied,
     * this method return the last item of itemset2. Otherwise it returns null.
     * @return the last item of itemset2, or null.
     * */
    public ItemU<T> allTheSameExceptLastItem(ItemsetU<T> itemset2) {
        // if not the same size, then return null
        if(itemset2.size() != items.size()){
            return null;
        }
        for(int i=0; i< items.size(); i++){
            // if they are the last items
            if(i == items.size()-1){
                // the one from items should be smaller (lexical order) and different than the one of itemset2
                if(items.get(i).getId().compareTo(itemset2.get(i).getId()) >= 0){
                    return null;
                }
            }
            // if they are not the last items, they  should be different
            else if(!items.get(i).getId().equals(itemset2.get(i).getId())){
                return null;
            }
        }
        return itemset2.get(itemset2.size()-1);
    }

    /**
     * Set the items in this itemsets.
     * @param items a list of items.
     */
    void setItems(List<ItemU<T>> items) {
        this.items = items;
    }


}