/** CombatLogEvent.java
 *
 *
 *
 *
 *
 */

public class CombatLogEvent extends CLEDescriptor implements Comparable<CombatLogEvent> {

    // 0 - timestamp, 1 - subevent, 2 - sourceID, 3 - destID
    // Prefix: 0 - spellID, 1 - spellSchool
    // Suffix: 0 - (int), 1 - (int), 2 - (int)

    public CombatLogEvent(double time, CLEDescriptor.PrefixParams prefixParams, CLEDescriptor.SuffixParams suffixParams, int sourceID, int destID) {
        super();
        this.time = time;
        this.prefix = prefixParams.prefix;
        this.suffix = suffixParams.suffix;
        this.sourceID = sourceID;
        this.destID = destID;

        for (int i = 0; i < TOTALPARAMS; i++) {
            params.add(-1);
        }

        this.params.set(0, prefixParams.spellID);
        this.params.set(1, prefixParams.spellSchool);

        this.params.set(2, suffixParams.param1);
        this.params.set(3, suffixParams.param2);
        this.params.set(4, suffixParams.param3);
        this.params.set(5, suffixParams.param4);
    }

    public void setSource(int ID) {
        this.sourceID = ID;
    }

    public void setDest(int ID) {
        this.destID = ID;
    }

    public double getTime() {
        return time;
    }


    /**
     *
     * Returns the value of the parameter with the given global ID
     *
     * @param pIndex
     * @return
     */
    public int getParam(int pIndex) {
        return params.get(pIndex);
    }

    public String toString() {
        String paramsStr = "";
        for (int i = 0; i < CLEDescriptor.TOTALPARAMS; i++) {
            paramsStr += "|" + Utils.rightPad(params.get(i) == -1 ? "" : String.valueOf(params.get(i)), 10);

        }

        return Utils.rightPad(Math.round(time*10)/10.0, 6) + "|" + Utils.rightPad(getSubEvent(), 20) +
                "|" + Utils.rightPad(sourceID, 10) + "|" + Utils.rightPad(destID, 10) + paramsStr;
    }

    public int compareTo(CombatLogEvent other) {
        return (this.time < other.time) ? -1 : 1;
    }
}
