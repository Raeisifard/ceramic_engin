
package BankIranWS.BankIranWS_NOR;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for FC87MSGOUT complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FC87MSGOUT">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;group ref="{http://v1.bankiran.org}gRETHTHDR"/>
 *         &lt;element name="ALERT_CODE">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="2"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="ALERT_MSG">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="34"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="OUT_CUSTOMER" type="{http://v1.bankiran.org}Fc87msgout_out__customer" maxOccurs="10" minOccurs="10"/>
 *         &lt;element name="CICS_XT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FC87MSGOUT", propOrder = {
    "fill0",
    "hostcicstransid",
    "hostseqno",
    "hostdest",
    "hostdatatype",
    "hostdatachain",
    "hostacknowledge",
    "hostmsgnbr",
    "hostemptysend",
    "flgglcd",
    "fill1",
    "alertcode",
    "alertmsg",
    "outcustomer",
    "cicsxt"
})
public class FC87MSGOUT {

    @XmlElement(name = "fill_0", required = true, defaultValue = " ", nillable = true)
    protected String fill0;
    @XmlElement(name = "HOST_CICS_TRANSID", required = true, defaultValue = " ", nillable = true)
    protected String hostcicstransid;
    @XmlElement(name = "HOST_SEQNO", required = true, defaultValue = " ", nillable = true)
    protected String hostseqno;
    @XmlElement(name = "HOST_DEST", required = true, defaultValue = " ", nillable = true)
    protected String hostdest;
    @XmlElement(name = "HOST_DATA_TYPE", required = true, defaultValue = " ", nillable = true)
    protected String hostdatatype;
    @XmlElement(name = "HOST_DATA_CHAIN", required = true, defaultValue = " ", nillable = true)
    protected String hostdatachain;
    @XmlElement(name = "HOST_ACKNOWLEDGE", required = true, defaultValue = " ", nillable = true)
    protected String hostacknowledge;
    @XmlElement(name = "HOST_MSGNBR", required = true, defaultValue = " ", nillable = true)
    protected String hostmsgnbr;
    @XmlElement(name = "HOST_EMPTY_SEND", required = true, defaultValue = " ", nillable = true)
    protected String hostemptysend;
    @XmlElement(name = "FLGGLCD", required = true, defaultValue = " ", nillable = true)
    protected String flgglcd;
    @XmlElement(name = "fill_1", required = true, defaultValue = " ", nillable = true)
    protected String fill1;
    @XmlElement(name = "ALERT_CODE", required = true, nillable = true)
    protected String alertcode;
    @XmlElement(name = "ALERT_MSG", required = true, nillable = true)
    protected String alertmsg;
    @XmlElement(name = "OUT_CUSTOMER", required = true, nillable = true)
    protected List<Fc87MsgoutOutCustomer> outcustomer;
    @XmlElement(name = "CICS_XT")
    protected String cicsxt;

    /**
     * Gets the value of the fill0 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFill0() {
        return fill0;
    }

    /**
     * Sets the value of the fill0 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFill0(String value) {
        this.fill0 = value;
    }

    /**
     * Gets the value of the hostcicstransid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHOSTCICSTRANSID() {
        return hostcicstransid;
    }

    /**
     * Sets the value of the hostcicstransid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHOSTCICSTRANSID(String value) {
        this.hostcicstransid = value;
    }

    /**
     * Gets the value of the hostseqno property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHOSTSEQNO() {
        return hostseqno;
    }

    /**
     * Sets the value of the hostseqno property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHOSTSEQNO(String value) {
        this.hostseqno = value;
    }

    /**
     * Gets the value of the hostdest property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHOSTDEST() {
        return hostdest;
    }

    /**
     * Sets the value of the hostdest property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHOSTDEST(String value) {
        this.hostdest = value;
    }

    /**
     * Gets the value of the hostdatatype property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHOSTDATATYPE() {
        return hostdatatype;
    }

    /**
     * Sets the value of the hostdatatype property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHOSTDATATYPE(String value) {
        this.hostdatatype = value;
    }

    /**
     * Gets the value of the hostdatachain property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHOSTDATACHAIN() {
        return hostdatachain;
    }

    /**
     * Sets the value of the hostdatachain property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHOSTDATACHAIN(String value) {
        this.hostdatachain = value;
    }

    /**
     * Gets the value of the hostacknowledge property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHOSTACKNOWLEDGE() {
        return hostacknowledge;
    }

    /**
     * Sets the value of the hostacknowledge property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHOSTACKNOWLEDGE(String value) {
        this.hostacknowledge = value;
    }

    /**
     * Gets the value of the hostmsgnbr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHOSTMSGNBR() {
        return hostmsgnbr;
    }

    /**
     * Sets the value of the hostmsgnbr property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHOSTMSGNBR(String value) {
        this.hostmsgnbr = value;
    }

    /**
     * Gets the value of the hostemptysend property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHOSTEMPTYSEND() {
        return hostemptysend;
    }

    /**
     * Sets the value of the hostemptysend property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHOSTEMPTYSEND(String value) {
        this.hostemptysend = value;
    }

    /**
     * Gets the value of the flgglcd property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFLGGLCD() {
        return flgglcd;
    }

    /**
     * Sets the value of the flgglcd property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFLGGLCD(String value) {
        this.flgglcd = value;
    }

    /**
     * Gets the value of the fill1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFill1() {
        return fill1;
    }

    /**
     * Sets the value of the fill1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFill1(String value) {
        this.fill1 = value;
    }

    /**
     * Gets the value of the alertcode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getALERTCODE() {
        return alertcode;
    }

    /**
     * Sets the value of the alertcode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setALERTCODE(String value) {
        this.alertcode = value;
    }

    /**
     * Gets the value of the alertmsg property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getALERTMSG() {
        return alertmsg;
    }

    /**
     * Sets the value of the alertmsg property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setALERTMSG(String value) {
        this.alertmsg = value;
    }

    /**
     * Gets the value of the outcustomer property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the outcustomer property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOUTCUSTOMER().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Fc87MsgoutOutCustomer }
     * 
     * 
     */
    public List<Fc87MsgoutOutCustomer> getOUTCUSTOMER() {
        if (outcustomer == null) {
            outcustomer = new ArrayList<Fc87MsgoutOutCustomer>();
        }
        return this.outcustomer;
    }

    /**
     * Gets the value of the cicsxt property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCICSXT() {
        return cicsxt;
    }

    /**
     * Sets the value of the cicsxt property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCICSXT(String value) {
        this.cicsxt = value;
    }

}
