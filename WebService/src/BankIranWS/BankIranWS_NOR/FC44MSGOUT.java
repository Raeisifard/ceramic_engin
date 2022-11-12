
package BankIranWS.BankIranWS_NOR;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for FC44MSGOUT complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FC44MSGOUT">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;group ref="{http://v1.bankiran.org}gRETHTHDR"/>
 *         &lt;element name="CUST_NAME1">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="66"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="CUST_NAME2">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="66"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="CURR_BAL">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="16"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="AVAL_BAL">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="16"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="EFFC_BAL">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="16"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="SHORT_NAME">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="24"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="ACCT_CONDITIONS">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="10"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="RESP_OFFICER">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="34"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
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
 *               &lt;maxLength value="68"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="OFFICER_COMM1">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="68"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="OFFICER_COMM2">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="68"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="OFFICER_COMM3">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="68"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="OFFICER_COMM4">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="68"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="OFFICER_COMM5">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="68"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="OFFICER_COMM6">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="68"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="ORIG_KEY">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="20"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="CHARGE_AMT44">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="15"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="PSBKFLAG">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="1"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="OPNBR" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="CICS-XT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FC44MSGOUT", propOrder = {
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
    "custname1",
    "custname2",
    "currbal",
    "avalbal",
    "effcbal",
    "shortname",
    "acctconditions",
    "respofficer",
    "alertcode",
    "alertmsg",
    "officercomm1",
    "officercomm2",
    "officercomm3",
    "officercomm4",
    "officercomm5",
    "officercomm6",
    "origkey",
    "chargeamt44",
    "psbkflag",
    "opnbr",
    "cicsxt"
})
public class FC44MSGOUT {

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
    @XmlElement(name = "CUST_NAME1", required = true, defaultValue = " ", nillable = true)
    protected String custname1;
    @XmlElement(name = "CUST_NAME2", required = true, defaultValue = " ", nillable = true)
    protected String custname2;
    @XmlElement(name = "CURR_BAL", required = true, defaultValue = " ", nillable = true)
    protected String currbal;
    @XmlElement(name = "AVAL_BAL", required = true, defaultValue = " ", nillable = true)
    protected String avalbal;
    @XmlElement(name = "EFFC_BAL", required = true, defaultValue = " ", nillable = true)
    protected String effcbal;
    @XmlElement(name = "SHORT_NAME", required = true, defaultValue = " ", nillable = true)
    protected String shortname;
    @XmlElement(name = "ACCT_CONDITIONS", required = true, defaultValue = " ", nillable = true)
    protected String acctconditions;
    @XmlElement(name = "RESP_OFFICER", required = true, defaultValue = " ", nillable = true)
    protected String respofficer;
    @XmlElement(name = "ALERT_CODE", required = true, defaultValue = " ", nillable = true)
    protected String alertcode;
    @XmlElement(name = "ALERT_MSG", required = true, defaultValue = " ", nillable = true)
    protected String alertmsg;
    @XmlElement(name = "OFFICER_COMM1", required = true)
    protected String officercomm1;
    @XmlElement(name = "OFFICER_COMM2", required = true)
    protected String officercomm2;
    @XmlElement(name = "OFFICER_COMM3", required = true)
    protected String officercomm3;
    @XmlElement(name = "OFFICER_COMM4", required = true)
    protected String officercomm4;
    @XmlElement(name = "OFFICER_COMM5", required = true)
    protected String officercomm5;
    @XmlElement(name = "OFFICER_COMM6", required = true)
    protected String officercomm6;
    @XmlElement(name = "ORIG_KEY", required = true, defaultValue = " ", nillable = true)
    protected String origkey;
    @XmlElement(name = "CHARGE_AMT44", required = true, defaultValue = " ", nillable = true)
    protected String chargeamt44;
    @XmlElement(name = "PSBKFLAG", required = true, defaultValue = " ", nillable = true)
    protected String psbkflag;
    @XmlElement(name = "OPNBR", required = true)
    protected String opnbr;
    @XmlElement(name = "CICS-XT")
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
     * Gets the value of the custname1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCUSTNAME1() {
        return custname1;
    }

    /**
     * Sets the value of the custname1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCUSTNAME1(String value) {
        this.custname1 = value;
    }

    /**
     * Gets the value of the custname2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCUSTNAME2() {
        return custname2;
    }

    /**
     * Sets the value of the custname2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCUSTNAME2(String value) {
        this.custname2 = value;
    }

    /**
     * Gets the value of the currbal property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCURRBAL() {
        return currbal;
    }

    /**
     * Sets the value of the currbal property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCURRBAL(String value) {
        this.currbal = value;
    }

    /**
     * Gets the value of the avalbal property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAVALBAL() {
        return avalbal;
    }

    /**
     * Sets the value of the avalbal property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAVALBAL(String value) {
        this.avalbal = value;
    }

    /**
     * Gets the value of the effcbal property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEFFCBAL() {
        return effcbal;
    }

    /**
     * Sets the value of the effcbal property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEFFCBAL(String value) {
        this.effcbal = value;
    }

    /**
     * Gets the value of the shortname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSHORTNAME() {
        return shortname;
    }

    /**
     * Sets the value of the shortname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSHORTNAME(String value) {
        this.shortname = value;
    }

    /**
     * Gets the value of the acctconditions property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getACCTCONDITIONS() {
        return acctconditions;
    }

    /**
     * Sets the value of the acctconditions property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setACCTCONDITIONS(String value) {
        this.acctconditions = value;
    }

    /**
     * Gets the value of the respofficer property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRESPOFFICER() {
        return respofficer;
    }

    /**
     * Sets the value of the respofficer property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRESPOFFICER(String value) {
        this.respofficer = value;
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
     * Gets the value of the officercomm1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOFFICERCOMM1() {
        return officercomm1;
    }

    /**
     * Sets the value of the officercomm1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOFFICERCOMM1(String value) {
        this.officercomm1 = value;
    }

    /**
     * Gets the value of the officercomm2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOFFICERCOMM2() {
        return officercomm2;
    }

    /**
     * Sets the value of the officercomm2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOFFICERCOMM2(String value) {
        this.officercomm2 = value;
    }

    /**
     * Gets the value of the officercomm3 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOFFICERCOMM3() {
        return officercomm3;
    }

    /**
     * Sets the value of the officercomm3 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOFFICERCOMM3(String value) {
        this.officercomm3 = value;
    }

    /**
     * Gets the value of the officercomm4 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOFFICERCOMM4() {
        return officercomm4;
    }

    /**
     * Sets the value of the officercomm4 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOFFICERCOMM4(String value) {
        this.officercomm4 = value;
    }

    /**
     * Gets the value of the officercomm5 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOFFICERCOMM5() {
        return officercomm5;
    }

    /**
     * Sets the value of the officercomm5 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOFFICERCOMM5(String value) {
        this.officercomm5 = value;
    }

    /**
     * Gets the value of the officercomm6 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOFFICERCOMM6() {
        return officercomm6;
    }

    /**
     * Sets the value of the officercomm6 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOFFICERCOMM6(String value) {
        this.officercomm6 = value;
    }

    /**
     * Gets the value of the origkey property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getORIGKEY() {
        return origkey;
    }

    /**
     * Sets the value of the origkey property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setORIGKEY(String value) {
        this.origkey = value;
    }

    /**
     * Gets the value of the chargeamt44 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCHARGEAMT44() {
        return chargeamt44;
    }

    /**
     * Sets the value of the chargeamt44 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCHARGEAMT44(String value) {
        this.chargeamt44 = value;
    }

    /**
     * Gets the value of the psbkflag property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPSBKFLAG() {
        return psbkflag;
    }

    /**
     * Sets the value of the psbkflag property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPSBKFLAG(String value) {
        this.psbkflag = value;
    }

    /**
     * Gets the value of the opnbr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOPNBR() {
        return opnbr;
    }

    /**
     * Sets the value of the opnbr property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOPNBR(String value) {
        this.opnbr = value;
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
