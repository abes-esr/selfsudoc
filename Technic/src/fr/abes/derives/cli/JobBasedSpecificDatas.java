package fr.abes.derives.cli;

import java.util.Set;

/**
 * 
 * will be processed in some cases with "html pivot/slk" workers
 * 
 * shortName : used in final transformation
 * 
 * @author michaux
 * 
 */
public class JobBasedSpecificDatas {

	private Set<String> excludedDataFields = null;
	private String pivotStyleSheetFileName = null;
	private String shortName = null;
	private boolean withCollections = false;
	
	public JobBasedSpecificDatas(Set<String> excludedDataFields, String pivotStyleSheetFileName, String shortName, boolean withCollections) {
		super();
		this.excludedDataFields = excludedDataFields;
		this.pivotStyleSheetFileName = pivotStyleSheetFileName;
		this.shortName = shortName;
		this.withCollections = withCollections;
	}

	public Set<String> getExcludedDataFields() {
		return excludedDataFields;
	}

	public String getPivotStyleSheetFileName() {
		return pivotStyleSheetFileName;
	}

	

	public String getShortName() {
		return shortName;
	}

	public boolean isWithCollections() {
		return withCollections;
	}

	@Override
	public String toString() {
		return "JobBasedSpecificDatas [excludedDataFields="
				+ excludedDataFields + ", pivotStyleSheetFileName="
				+ pivotStyleSheetFileName + ", shortName=" + shortName
				+ ", withCollections=" + withCollections + "]";
	}

}
