import java.util.ArrayList;

/** CLETemplate.java
 *
 * Template for matching CLEs by only certain fields
 *
 * Used for CLE triggers to determine whether a condition has been met
 *
 */

public class CLETemplate extends CLEDescriptor {

    // prefix and suffix must be specified
    public CLETemplate(CLEDescriptor.Prefix prefix, CLEDescriptor.Suffix suffix) {
        super();
        this.time = -2.0;
        this.prefix = prefix;
        this.suffix = suffix;
        this.sourceID = -2;
        this.destID = -2;

        // >=0 = must equal
        // -1 = must be empty
        // -2 = do not watch
        for (int i = 0; i < TOTALPARAMS; i++) {
            params.add(-2);
        }

    }

    public void watchTime(double time) {
        this.time = time;
    }

    public void watchPrefix(CLEDescriptor.Prefix prefix) {
        this.prefix = prefix;
    }

    public void watchSuffix(CLEDescriptor.Suffix suffix) {
        this.suffix = suffix;
    }

    public void watchSource(int sourceID) {
        this.sourceID = sourceID;
    }

    public void watchDest(int destID) {
        this.destID = destID;
    }


    /** watchParamIndex
     *
     * Sets the template to watch the value of the parameter with given index
     *
     * @param i
     */
    public void watchParamIndex(int i, int val) {
        this.params.set(i, val);
    }


    /** describes
     *
     * Returns whether this template describes the passed CLE. A template describes a CLE when the template's watched
     * values are equal to the CLE's values, and unwatched values have no effect
     *
     * @param cle
     * @return
     */
    public boolean describes(CombatLogEvent cle) {
        //System.out.println(prefix + "........" + cle.prefix);
        //System.out.println(suffix + "........" + cle.suffix);
        //System.out.println(params.get(0) + "........" + cle.params.get(0));

        // compare primary fields
        boolean primaryCompare = (this.time == -2.0 || this.time == cle.time) &&
                (this.prefix == null || this.prefix == cle.prefix) &&
                (this.suffix == null || this.suffix == cle.suffix) &&
                (this.sourceID == -2 || this.sourceID == cle.sourceID) &&
                (this.destID == -2 || this.destID == cle.destID);

        // compare params
        boolean paramCompare = true;
        for (int i = 0; i < CLEDescriptor.TOTALPARAMS; i++) {
            if (params.get(i) == -2) {
                continue;
            }

            //System.out.println(params.get(i) + "  " + cle.params.get(i));
            paramCompare = paramCompare && (params.get(i).equals(cle.params.get(i)));
        }

        //System.out.println(primaryCompare + "  " + paramCompare);

        return primaryCompare && paramCompare;

    }
}
