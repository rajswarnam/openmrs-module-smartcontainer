package org.openmrs.module.smartcontainer.smartData;

/**
 * The class representing SMART datatype
 * 
 *  <a
 *      href="http://wiki.chip.org/smart-project/index.php/Preview_Data_Model#ValueAndUnit_RDF">
 *      ValueAndUnit</a>
 *
 */
public class ValueAndUnit {
	private String value;
	private String unit;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

}