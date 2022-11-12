
package BankIranWS.BankIranWS_NOR;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MB60MSGOUT complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MB60MSGOUT">
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
 *         &lt;element name="BRNCH_NO">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="4"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="ACCOUNT_STAT_CODE">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="2"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="ACCOUNT_STAT_DESC">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="30"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="ACCOUNT_STYP_CODE">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="3"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="ACCOUNT_STYP_DESC">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="40"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="CUST_TYPE">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="2"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="ID_NUMBER">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="12"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="CUST_F_NAME">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="66"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="CUST_L_NAME">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="66"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="SB_OPENRES">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="2"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="ALERT_MSGE" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="SB_ACCT_CNTL" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="SB_ACCT_CNTL_DB" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="SB_SICCODE" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="SB_SECCODE" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="SB_CUSNO" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="SB_ACSUBTYP" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="CUST_SUBTYP" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="CUST_NATCD" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ACCT_DATEOPN" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
@XmlType(name = "MB60MSGOUT", propOrder = {
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
    "brnchno",
    "accountstatcode",
    "accountstatdesc",
    "accountstypcode",
    "accountstypdesc",
    "custtype",
    "idnumber",
    "custfname",
    "custlname",
    "sbopenres",
    "alertmsge",
    "sbacctcntl",
    "sbacctcntldb",
    "sbsiccode",
    "sbseccode",
    "sbcusno",
    "sbacsubtyp",
    "custsubtyp",
    "custnatcd",
    "acctdateopn",
    "cicsxt"
})
public class MB60MSGOUT {

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
    @XmlElement(name = "ALERT_CODE", required = true, defaultValue = " ", nillable = true)
    protected String alertcode;
    @XmlElement(name = "BRNCH_NO", required = true, defaultValue = " ", nillable = true)
    protected String brnchno;
    @XmlElement(name = "ACCOUNT_STAT_CODE", required = true, defaultValue = " ", nillable = true)
    protected String accountstatcode;
    @XmlElement(name = "ACCOUNT_STAT_DESC", required = true, defaultValue = " ", nillable = true)
    protected String accountstatdesc;
    @XmlElement(name = "ACCOUNT_STYP_CODE", required = true, defaultValue = " ", nillable = true)
    protected String accountstypcode;
    @XmlElement(name = "ACCOUNT_STYP_DESC", required = true, defaultValue = " ", nillable = true)
    protected String accountstypdesc;
    @XmlElement(name = "CUST_TYPE", required = true, defaultValue = " ", nillable = true)
    protected String custtype;
    @XmlElement(name = "ID_NUMBER", required = true, defaultValue = " ", nillable = true)
    protected String idnumber;
    @XmlElement(name = "CUST_F_NAME", required = true, defaultValue = " ", nillable = true)
    protected String custfname;
    @XmlElement(name = "CUST_L_NAME", required = true, defaultValue = " ", nillable = true)
    protected String custlname;
    @XmlElement(name = "SB_OPENRES", required = true, nillable = true)
    protected String sbopenres;
    @XmlElement(name = "ALERT_MSGE", required = true, defaultValue = " ")
    protected String alertmsge;
    @XmlElement(name = "SB_ACCT_CNTL", required = true)
    protected String sbacctcntl;
    @XmlElement(name = "SB_ACCT_CNTL_DB", required = true)
    protected String sbacctcntldb;
    @XmlElement(name = "SB_SICCODE", required = true)
    protected String sbsiccode;
    @XmlElement(name = "SB_SECCODE", required = true)
    protected String sbseccode;
    @XmlElement(name = "SB_CUSNO", required = true)
    protected String sbcusno;
    @XmlElement(name = "SB_ACSUBTYP", required = true)
    protected String sbacsubtyp;
    @XmlElement(name = "CUST_SUBTYP", required = true)
    protected String custsubtyp;
    @XmlElement(name = "CUST_NATCD", required = true)
    protected String custnatcd;
    @XmlElement(name = "ACCT_DATEOPN", required = true)
    protected String acctdateopn;
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
     * Gets the value of the brnchno property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBRNCHNO() {
        return brnchno;
    }

    /**
     * Sets the value of the brnchno property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBRNCHNO(String value) {
        this.brnchno = value;
    }

    /**
     * Gets the value of the accountstatcode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getACCOUNTSTATCODE() {
        return accountstatcode;
    }

    /**
     * Sets the value of the accountstatcode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setACCOUNTSTATCODE(String value) {
        this.accountstatcode = value;
    }

    /**
     * Gets the value of the accountstatdesc property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getACCOUNTSTATDESC() {
        return accountstatdesc;
    }

    /**
     * Sets the value of the accountstatdesc property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setACCOUNTSTATDESC(String value) {
        this.accountstatdesc = value;
    }

    /**
     * Gets the value of the accountstypcode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getACCOUNTSTYPCODE() {
        return accountstypcode;
    }

    /**
     * Sets the value of the accountstypcode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setACCOUNTSTYPCODE(String value) {
        this.accountstypcode = value;
    }

    /**
     * Gets the value of the accountstypdesc property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getACCOUNTSTYPDESC() {
        return accountstypdesc;
    }

    /**
     * Sets the value of the accountstypdesc property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setACCOUNTSTYPDESC(String value) {
        this.accountstypdesc = value;
    }

    /**
     * Gets the value of the custtype property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCUSTTYPE() {
        return custtype;
    }

    /**
     * Sets the value of the custtype property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCUSTTYPE(String value) {
        this.custtype = value;
    }

    /**
     * Gets the value of the idnumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIDNUMBER() {
        return idnumber;
    }

    /**
     * Sets the value of the idnumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIDNUMBER(String value) {
        this.idnumber = value;
    }

    /**
     * Gets the value of the custfname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCUSTFNAME() {
        return custfname;
    }

    /**
     * Sets the value of the custfname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCUSTFNAME(String value) {
        this.custfname = value;
    }

    /**
     * Gets the value of the custlname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCUSTLNAME() {
        return custlname;
    }

    /**
     * Sets the value of the custlname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCUSTLNAME(String value) {
        this.custlname = value;
    }

    /**
     * Gets the value of the sbopenres property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSBOPENRES() {
        return sbopenres;
    }

    /**
     * Sets the value of the sbopenres property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSBOPENRES(String value) {
        this.sbopenres = value;
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
     * Gets the value of the sbacctcntl property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSBACCTCNTL() {
        return sbacctcntl;
    }

    /**
     * Sets the value of the sbacctcntl property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSBACCTCNTL(String value) {
        this.sbacctcntl = value;
    }

    /**
     * Gets the value of the sbacctcntldb property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSBACCTCNTLDB() {
        return sbacctcntldb;
    }

    /**
     * Sets the value of the sbacctcntldb property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSBACCTCNTLDB(String value) {
        this.sbacctcntldb = value;
    }

    /**
     * Gets the value of the sbsiccode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSBSICCODE() {
        return sbsiccode;
    }

    /**
     * Sets the value of the sbsiccode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSBSICCODE(String value) {
        this.sbsiccode = value;
    }

    /**
     * Gets the value of the sbseccode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSBSECCODE() {
        return sbseccode;
    }

    /**
     * Sets the value of the sbseccode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSBSECCODE(String value) {
        this.sbseccode = value;
    }

    /**
     * Gets the value of the sbcusno property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSBCUSNO() {
        return sbcusno;
    }

    /**
     * Sets the value of the sbcusno property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSBCUSNO(String value) {
        this.sbcusno = value;
    }

    /**
     * Gets the value of the sbacsubtyp property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSBACSUBTYP() {
        return sbacsubtyp;
    }

    /**
     * Sets the value of the sbacsubtyp property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSBACSUBTYP(String value) {
        this.sbacsubtyp = value;
    }

    /**
     * Gets the value of the custsubtyp property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCUSTSUBTYP() {
        return custsubtyp;
    }

    /**
     * Sets the value of the custsubtyp property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCUSTSUBTYP(String value) {
        this.custsubtyp = value;
    }

    /**
     * Gets the value of the custnatcd property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCUSTNATCD() {
        return custnatcd;
    }

    /**
     * Sets the value of the custnatcd property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCUSTNATCD(String value) {
        this.custnatcd = value;
    }

    /**
     * Gets the value of the acctdateopn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getACCTDATEOPN() {
        return acctdateopn;
    }

    /**
     * Sets the value of the acctdateopn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setACCTDATEOPN(String value) {
        this.acctdateopn = value;
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
