
package BankIranWS.BankIranWS_NOR;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for tFlt complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tFlt">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="fcode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="fdesc" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tFlt", propOrder = {
    "fcode",
    "fdesc"
})
public class TFlt {

    @XmlElement(required = true)
    protected String fcode;
    @XmlElement(required = true)
    protected String fdesc;

    /**
     * Gets the value of the fcode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFcode() {
        return fcode;
    }

    /**
     * Sets the value of the fcode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFcode(String value) {
        this.fcode = value;
    }

    /**
     * Gets the value of the fdesc property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFdesc() {
        return fdesc;
    }

    /**
     * Sets the value of the fdesc property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFdesc(String value) {
        this.fdesc = value;
    }

}
