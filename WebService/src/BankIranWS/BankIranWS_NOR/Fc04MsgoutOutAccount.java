
package BankIranWS.BankIranWS_NOR;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Fc04msgout_out__account complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Fc04msgout_out__account">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;group ref="{http://v1.bankiran.org}fc04msgout_out__account"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Fc04msgout_out__account", propOrder = {
    "outaccountno",
    "outaccountstat",
    "outaccountavbal",
    "outopnbr"
})
public class Fc04MsgoutOutAccount {

    @XmlElement(name = "OUT_ACCOUNT_NO", required = true, defaultValue = " ", nillable = true)
    protected String outaccountno;
    @XmlElement(name = "OUT_ACCOUNT_STAT", required = true, defaultValue = " ", nillable = true)
    protected String outaccountstat;
    @XmlElement(name = "OUT_ACCOUNT_AVBAL", required = true, defaultValue = " ", nillable = true)
    protected String outaccountavbal;
    @XmlElement(name = "OUT_OPNBR", required = true)
    protected String outopnbr;

    /**
     * Gets the value of the outaccountno property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOUTACCOUNTNO() {
        return outaccountno;
    }

    /**
     * Sets the value of the outaccountno property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOUTACCOUNTNO(String value) {
        this.outaccountno = value;
    }

    /**
     * Gets the value of the outaccountstat property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOUTACCOUNTSTAT() {
        return outaccountstat;
    }

    /**
     * Sets the value of the outaccountstat property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOUTACCOUNTSTAT(String value) {
        this.outaccountstat = value;
    }

    /**
     * Gets the value of the outaccountavbal property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOUTACCOUNTAVBAL() {
        return outaccountavbal;
    }

    /**
     * Sets the value of the outaccountavbal property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOUTACCOUNTAVBAL(String value) {
        this.outaccountavbal = value;
    }

    /**
     * Gets the value of the outopnbr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOUTOPNBR() {
        return outopnbr;
    }

    /**
     * Sets the value of the outopnbr property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOUTOPNBR(String value) {
        this.outopnbr = value;
    }

}
