package fr.abes.derives.extract;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class OrderWrapper {

    final protected static String PDF = "pdf";
    final protected static String RTF = "rtf";
    final protected static String SYLK = "slk";

    protected final static String RCR_FILE_PREFIX = "notices";
    protected final static String COLUMN_PREFIX = "ETAT_";

    private boolean withCollections = false;
    private Map<String, Set<String>> rcrS = null; //store RCRs dispatched by key=final format (pdf, rtf...)

    //optional limits per RCR
    private Map<String, Integer> lowLimitMap = new HashMap<String, Integer>();
    private Map<String, Integer> highLimitMap = new HashMap<String, Integer>();

    public OrderWrapper(boolean withCollections) {
        super();
        this.withCollections = withCollections;
        this.rcrS = new HashMap<String, Set<String>>();
        Set<String> rcrPDF = new HashSet<String>();
        Set<String> rcrRTF = new HashSet<String>();
        Set<String> rcrSYLK = new HashSet<String>();
        this.rcrS.put(PDF, rcrPDF);
        this.rcrS.put(RTF, rcrRTF);
        this.rcrS.put(SYLK, rcrSYLK);
    }

    public boolean isWithCollections() {
        return withCollections;
    }

    public Map<String, Set<String>> getRcrS() {
        return rcrS;
    }

    @Override
    public String toString() {
        return "Order [rcrS=" + rcrS.values() + ", withCollections=" + withCollections + "]";
    }

    public void addRcr(String finalFormat, String rcr, Integer lowLimit, Integer highLimit) {
        this.rcrS.get(finalFormat).add(rcr);
        if (lowLimit != null) {
            this.lowLimitMap.put(rcr, lowLimit);
        }
        if (highLimit != null) {
            this.highLimitMap.put(rcr, highLimit);
        }
    }

    public Integer getLowLimit(String rcr) {
        return this.lowLimitMap.get(rcr);
    }

    public Integer getHighLimit(String rcr) {
        return this.highLimitMap.get(rcr);
    }

    public Map<String, Integer> getLowLimitMap() {
        return lowLimitMap;
    }

    public Map<String, Integer> getHighLimitMap() {
        return highLimitMap;
    }
}
