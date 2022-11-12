
package BankIranWS.BankIranWS_NOR;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for FC04MSGOUT complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FC04MSGOUT">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;group ref="{http://v1.bankiran.org}gRETHTHDR"/>
 *         &lt;element name="OUT_NAME1">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="66"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="OUT_NAME2">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="66"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="OUT_DATEOPN">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="8"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="OUT_FATHERNAME">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="60"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="OUT_ECONOMICOD">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="10"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="OUT_IDNUM">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="12"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="OUT_CUSNO">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="10"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="OUT_ISSUED_TOWN" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="OUT_BRANCHNO" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="OUT_ACCOUNT" type="{http://v1.bankiran.org}Fc04msgout_out__account" maxOccurs="10" minOccurs="10"/>
 *         &lt;element name="ALERT_CODE">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="2"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="ALERT_MSGE">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="68"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
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
@XmlType(name = "FC04MSGOUT", propOrder = {
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
    "outname1",
    "outname2",
    "outdateopn",
    "outfathername",
    "outeconomicod",
    "outidnum",
    "outcusno",
    "outissuedtown",
    "outbranchno",
    "outaccount",
    "alertcode",
    "alertmsge",
    "cicsxt"
})
public class FC04MSGOUT {

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
    @XmlElement(name = "OUT_NAME1", required = true, defaultValue = " ", nillable = true)
    protected String outname1;
    @XmlElement(name = "OUT_NAME2", required = true, defaultValue = " ", nillable = true)
    protected String outname2;
    @XmlElement(name = "OUT_DATEOPN", required = true, defaultValue = " ", nillable = true)
    protected String outdateopn;
    @XmlElement(name = "OUT_FATHERNAME", required = true, defaultValue = " ", nillable = true)
    protected String outfathername;
    @XmlElement(name = "OUT_ECONOMICOD", required = true, defaultValue = " ", nillable = true)
    protected String outeconomicod;
    @XmlElement(name = "OUT_IDNUM", required = true, defaultValue = " ", nillable = true)
    protected String outidnum;
    @XmlElement(name = "OUT_CUSNO", required = true, defaultValue = " ", nillable = true)
    protected String outcusno;
    @XmlElement(name = "OUT_ISSUED_TOWN", required = true)
    protected String outissuedtown;
    @XmlElement(name = "OUT_BRANCHNO", required = true)
    protected String outbranchno;
    @XmlElement(name = "OUT_ACCOUNT", required = true, nillable = true)
    protected List<Fc04MsgoutOutAccount> outaccount;
    @XmlElement(name = "ALERT_CODE", required = true, defaultValue = " ", nillable = true)
    protected String alertcode;
    @XmlElement(name = "ALERT_MSGE", required = true, defaultValue = " ", nillable = true)
    protected String alertmsge;
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
     * Gets the value of the outname1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOUTNAME1() {
        return outname1;
    }

    /**
     * Sets the value of the outname1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOUTNAME1(String value) {
        this.outname1 = value;
    }

    /**
     * Gets the value of the outname2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOUTNAME2() {
        return outname2;
    }

    /**
     * Sets the value of the outname2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOUTNAME2(String value) {
        this.outname2 = value;
    }

    /**
     * Gets the value of the outdateopn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOUTDATEOPN() {
        return outdateopn;
    }

    /**
     * Sets the value of the outdateopn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOUTDATEOPN(String value) {
        this.outdateopn = value;
    }

    /**
     * Gets the value of the outfathername property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOUTFATHERNAME() {
        return outfathername;
    }

    /**
     * Sets the value of the outfathername property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOUTFATHERNAME(String value) {
        this.outfathername = value;
    }

    /**
     * Gets the value of the outeconomicod property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOUTECONOMICOD() {
        return outeconomicod;
    }

    /**
     * Sets the value of the outeconomicod property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOUTECONOMICOD(String value) {
        this.outeconomicod = value;
    }

    /**
     * Gets the value of the outidnum property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOUTIDNUM() {
        return outidnum;
    }

    /**
     * Sets the value of the outidnum property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOUTIDNUM(String value) {
        this.outidnum = value;
    }

    /**
     * Gets the value of the outcusno property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOUTCUSNO() {
        return outcusno;
    }

    /**
     * Sets the value of the outcusno property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOUTCUSNO(String value) {
        this.outcusno = value;
    }

    /**
     * Gets the value of the outissuedtown property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOUTISSUEDTOWN() {
        return outissuedtown;
    }

    /**
     * Sets the value of the outissuedtown property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOUTISSUEDTOWN(String value) {
        this.outissuedtown = value;
    }

    /**
     * Gets the value of the outbranchno property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOUTBRANCHNO() {
        return outbranchno;
    }

    /**
     * Sets the value of the outbranchno property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOUTBRANCHNO(String value) {
        this.outbranchno = value;
    }

    /**
     * Gets the value of the outaccount property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the outaccount property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOUTACCOUNT().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Fc04MsgoutOutAccount }
     * 
     * 
     */
    public List<Fc04MsgoutOutAccount> getOUTACCOUNT() {
        if (outaccount == null) {
            outaccount = new ArrayList<Fc04MsgoutOutAccount>();
        }
        return this.outaccount;
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
     * Gets the value of the alertmsge property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getALERTMSGE() {
        return alertmsge;
    }

    /**
     * Sets the value of the alertmsge property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setALERTMSGE(String value) {
        this.alertmsge = value;
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
