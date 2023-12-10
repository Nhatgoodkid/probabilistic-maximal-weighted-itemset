package pattern;

public abstract class AbstractOrderedItemset extends AbstractItemset {

    public AbstractOrderedItemset() {
        super();
    }

    /**
     * Get the support of this itemset
     */
    public abstract int getAbsoluteSupport();

    /**
     * Get the size of this itemset
     */
    public abstract int size();

    /**
     * Get the item at a given position of this itemset
     */
    public abstract Integer get(int position);

    /**
     * Get the last item.
     */
    public Integer getLastItem() {
        return get(size() - 1);
    }

    /**
     * Get this itemset as a string
     */
    public String toString(){
        if(size() == 0) {
            return "EMPTYSET";
        }
        // use a string buffer for more efficiency
        StringBuilder r = new StringBuilder ();
        // for each item, append it to the StringBuilder
        for(int i=0; i< size(); i++){
            r.append(get(i));
            r.append(' ');
        }
        return r.toString(); // return the tring
    }


    /**
     * Get the relative support of this itemset (a percentage) as a double
     */
    public double getRelativeSupport(int nbObject) {
        // Divide the absolute support by the number of transactions to get the relative support
        return ((double)getAbsoluteSupport()) / ((double) nbObject);
    }


    /**
     * Check if this itemset contains a given item.
     */
    public boolean contains(Integer item) {
        for (int i=0; i< size(); i++) {
            if (get(i).equals(item)) {
                return true;
            } else if (get(i) > item) {
                return false;
            }
        }
        return false;
    }


}
